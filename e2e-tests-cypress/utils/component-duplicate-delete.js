const https = require('https');
let hostname;

const projectName = "Default Project";

const args = process.argv.slice(2);

if (args.length == 4) {
  const env = args[0];

  switch (env) {
    case "dev":
      hostname = "apis.preview-dv.choreo.dev";
      break;
    case "stage":
      hostname = "apis.st.choreo.dev";
      break;
    case "prod":
      hostname = "apis.choreo.dev";
      break;
    default:
      printUsage();
  }

  const token = args[1];
  const jwtPayload = parseJwt(token);
  const orgId = args[2];
  const orgHandler = jwtPayload.organization.handle;
  const componentNamePrefix = args[3];
  deleteComponents(token, orgId, orgHandler, projectName, componentNamePrefix);
} else {
  printUsage();
}

function deleteComponents(token, orgId, orgHandler, projectName, componentNamePrefix) {
  getComponents(token, orgId, orgHandler, projectName, componentNamePrefix, function(matchingComponents) {
    console.log(`matchingComponents count: ${matchingComponents.length}`);

    let deletedCount = 0;

    matchingComponents.forEach(component => {
      if (component.displayName !== componentNamePrefix) {
        deleteComponent(token, component.orgHandler, component.id, component.projectId);
        deletedCount++;
      }
    });

    console.log(`deletedCount: ${deletedCount}`);
  });
}

async function deleteComponent(token, orgHandler, componentId, projectId) {
  await delay(3000);
  let body = JSON.stringify({ query: `mutation{ deleteComponentV2(orgHandler: "${orgHandler}", componentId: "${componentId}", projectId: "${projectId}")` +
  `{ status, canDelete, message, encodedData }}`});

  sendGraphQLPost(token, body, function(response){
    console.log(`deleteComponent response: ${JSON.stringify(response)}`);
  });
}

function getComponents(token, orgId, orgHandler, projectName, componentNamePrefix, callback) {
  getProject(orgId, token, projectName, function(project){
    let body = JSON.stringify({ query: `query{ components(orgHandler:  "${orgHandler}", projectId: "${project.id}" )` +
    `{\n projectId, \n id, \n description, \n name, \n handler,` +
      `\n displayName, \n displayType, \n version, \n createdAt,` +
      `\n lastBuildDate,\n orgHandler, \n apiVersions { \n apiVersion,` +
        `\n proxyName,\n proxyUrl,\n proxyId,\n id,\n state,\n latest,` +
        `\n branch,\n accessibility\n }\n } \n }` });

      sendGraphQLPost(token, body, function(response){
        // For debugging
        //console.log(`getComponents reponse: ${JSON.stringify(response)}`);

        let componentsList = [];
        response.data.components.forEach(component => {
          if (component.displayName.startsWith(componentNamePrefix)) {
            componentsList.push(component);
          }
        });

        callback(componentsList);
       });
  });
}

async function getProject(orgId, token, projectName, callback) {

  let body = JSON.stringify({
    query: `query{projects(orgId: ${orgId}){      id, orgId, name, version, createdDate, handler, region, description    }}`
  });

  await sendGraphQLPost(token, body, function(response){
    // For debugging
    //console.log(`\ngetProject reponse: ${JSON.stringify(response)}\n`);

    response.data.projects.forEach(project => {
      if (project.name == projectName) {
        callback(project);
      }
    });
  })
}

async function sendGraphQLPost(token, body, callback) {
  // For debugging
  //console.log(`\nsendGraphQLPost body: ${body}\n`)

  const options = {
    hostname: hostname,
    port: 443,
    path: `/projects/1.0.0/graphql`,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`,
      'Content-Length': Buffer.byteLength(body)
    },
  };

  const res = await httpsPost({body,
    options
  })

  return callback(res);
}

function httpsPost({body, options}) {
  return new Promise((resolve,reject) => {
      const req = https.request(options, res => {
          const chunks = [];
          res.on('data', data => chunks.push(data))
          res.on('end', () => {
              let resBody = Buffer.concat(chunks);
              switch(res.headers['content-type']) {
                  case 'application/json':
                      resBody = JSON.parse(resBody);
                      break;
              }
              resolve(resBody)
          })
      })
      req.on('error',reject);
      if(body) {
          req.write(body);
      }
      req.end();
  })
}


function parseJwt(token) {
  return JSON.parse(Buffer.from(token.split('.')[1], 'base64').toString());
}

function delay(time) {
  return new Promise(resolve => setTimeout(resolve, time));
}

function printUsage() {
  console.log("\n");
  console.log("usage: node component-duplicate-delete.js {dev|stage|prod} <access_token> <orgId> <component_name_prefix>");
  process.exit(1);
}