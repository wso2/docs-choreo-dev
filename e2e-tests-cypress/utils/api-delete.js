const https = require('https');
let hostname = "sts.choreo.dev";

const args = process.argv.slice(2);

if (args.length == 2) {
  const env = args[0];

  switch (env) {
    case "dev":
      hostname = "sts.preview-dv.choreo.dev";
      break;
    case "stage":
      hostname = "sts.st.choreo.dev";
      break;
    case "prod":
      hostname = "sts.choreo.dev";
      break;
    default:
      printUsage();
  }

  const token = args[1];
  const jwtPayload = parseJwt(token);
  const orgId = jwtPayload.organization.uuid;
  let offset = 0;
  let limit =  25;
  deleteApis(token, orgId, offset, limit);
} else {
  printUsage();
}

function deleteApis(token, orgId, offset, limit) {
  let e2eApis = [];
 getApis(token, orgId, offset, limit, e2eApis);
}

function getApis(token, orgId, offset, limit, apisList = []) {
  const apiNamePrefix = "automationtestcomponent";
  const oldApiNamePrefix = "e2eapi";

  const options = {
    hostname: hostname,
    port: 443,
    path: `/api/am/publisher/v2/apis?organizationId=${orgId}&offset=${offset}&limit=${limit}`,
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  const req = https.request(options, res => {
    if (res.statusCode == 401) {
      console.log("Access token has expired")
      process.exit(1);
    }

    res.setEncoding('utf8');
    let rawData = '';
    res.on('data', (chunk) => { rawData += chunk; });
    res.on('end', () => {
      let jsonParsed = JSON.parse(rawData)
      const apis = jsonParsed.list;
      const count = jsonParsed.count;

      if (count > 0) {
        console.log(`getApis count: ${count}`);

        const e2eApis = apis.filter(({ name }) => name.includes(apiNamePrefix) || name.includes(oldApiNamePrefix));
        apisList.push.apply(apisList, e2eApis);
        getApis(token, orgId, offset + limit, limit, apisList);
      } else {
        console.log(`Total number of APIs to be removed: ${apisList.length}`);

        for (const api of apisList) {
          if (isApiRemovable(api.name, apiNamePrefix, oldApiNamePrefix)) {
            deleteSubscriptions(token, orgId, api.id);
            unpublishApi(token, orgId, api.id);
            deleteApi(token, orgId, api.id);
          }
        }
      }
    });
  });

  req.on('error', error => {
    console.error(error);
  });

  req.end();
}


async function deleteApi(token, orgId, apiId) {
  await delay(300);
  const https = require('https');

  const options = {
    hostname: hostname,
    port: 443,
    path: `/api/am/publisher/v2/apis/${apiId}?organizationId=${orgId}`,
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  const req = https.request(options, res => {
    console.log(`statusCode: ${res.statusCode}`);
  });

  req.on('error', error => {
    console.error(error);
  });

  req.end();
}

function deleteSubscriptions(token, orgId, apiId) {
  const options = {
    hostname: hostname,
    port: 443,
    path: `/api/am/publisher/v2/subscriptions?organizationId=${orgId}&apiId=${apiId}`,
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  const req = https.request(options, res => {
    console.log(`Get subscriptions statusCode: ${res.statusCode}`);

    res.setEncoding('utf8');
    let rawData = '';
    res.on('data', (chunk) => { rawData += chunk; });
    res.on('end', () => {
      try {
        let jsonParsed = JSON.parse(rawData)

        const subscriptions = jsonParsed.list;
        const count = jsonParsed.count;

        if (count > 0) {
          for (const subscription of subscriptions) {
            deleteSubscription(token, orgId, subscription.subscriptionId);
          }
        }
      } catch(e) {
        console.error(e);
      }
    });
  });

  req.on('error', error => {
    console.error(error);
  });

  req.end();
}

async function deleteSubscription(token, orgId, subscriptionId) {
  await delay(300);
  const https = require('https');

  const options = {
    hostname: hostname,
    port: 443,
    path: `/api/am/devportal/v2/subscriptions/${subscriptionId}/?organizationId=${orgId}`,
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  const req = https.request(options, res => {
    console.log(`statusCode: ${res.statusCode}`);
  });

  req.on('error', error => {
    console.error(error);
  });

  req.end();
}

function unpublishApi(token, orgId, apiId) {
  const https = require('https');

  const options = {
    hostname: hostname,
    port: 443,
    path: `/api/am/publisher/v2/apis/change-lifecycle?organizationId=${orgId}&apiId=${apiId}&action=Created`,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
    },
  };

  const req = https.request(options, res => {
    console.log(`unpublishApi statusCode: ${res.statusCode}`);
  });

  req.on('error', error => {
    console.error(error);
  });

  req.end();
}

function parseJwt(token) {
  return JSON.parse(Buffer.from(token.split('.')[1], 'base64').toString());
}

function delay(time) {
  return new Promise(resolve => setTimeout(resolve, time));
}

function isApiRemovable(name, apiNamePrefix, oldApiNamePrefix) {
  if (name.includes(oldApiNamePrefix)) {
    return true;
  }

  if (name.includes(apiNamePrefix)) {
    // Extract date section of project name for comparison
    let remainder = name.split(apiNamePrefix)[1];

    let i = 0;
    let isNonNumericPostfix = false;
    while (i < remainder.length) {
        if (isNaN(remainder[i])) {
          isNonNumericPostfix = true;
          break;
        }
        i++;
    }

    if (isNonNumericPostfix) {
      remainder = remainder.slice(0,i);
    }

    const createdDate = Number(remainder);

    const diff = Date.now() - createdDate

    // Only delete projects(and their components) that are older than 1 hour
    if (diff > 60 * 60 * 1000) {
      return true;
    }
  }

  return false;
}

function printUsage() {
  console.log("\n");
  console.log("usage: node api-delete.js {dev|stage|prod} <access_token>")
  process.exit(1);
}
