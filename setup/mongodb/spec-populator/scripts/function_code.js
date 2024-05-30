import axios from "axios";

let tokenInfo = {
  accessToken: null,
  expiryTime: null
};

async function getAccessToken(clientId, clientSecret, tokenEndpoint) {

  console.log(`Fetching access token from ${tokenEndpoint}`);

  const requestBody = {
    grant_type: 'client_credentials'
  };

  const credentials = Buffer.from(`${clientId}:${clientSecret}`).toString('base64');

  const config = {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
      'Authorization': `Basic ${credentials}`
    }
  };

  try {
    let currentTime = new Date().getTime();
    const response = await axios.post(tokenEndpoint, requestBody, config);
    const expiresInMilliseconds = response.data.expires_in * 1000;

    // Store the token and its expiry time
    tokenInfo.accessToken = `Bearer ${response.data.access_token}`;
    tokenInfo.expiryTime = currentTime + expiresInMilliseconds;
    
    return tokenInfo.accessToken;
  } catch (error) {
    console.error(`Failed to fetch access token: ${error}`);
  }
}

exports = async function (changeEvent) {
  
  const consumerKey = context.values.get("consumerKey");
  const consumerSecret = context.values.get("consumerSecretValue");
  const tokenEndpoint = context.values.get("tokenEndpoint");
  const externalURL = context.values.get("specPopulatorUrl");

  const util = require('util');

  console.log("Function triggered - changeEvent: ", changeEvent.operationType);
  
  let accessToken = null;

  let currentTime = new Date().getTime();
  if (!tokenInfo.accessToken || !tokenInfo.expiryTime || (tokenInfo.accessToken && currentTime+10 >= tokenInfo.expiryTime)) {
    accessToken = await getAccessToken(consumerKey, consumerSecret, tokenEndpoint);
  } else {
    accessToken = tokenInfo.accessToken;
  }

  if (!accessToken) {
    console.error('Failed to get access token');
    return;
  }
  
  let docId = changeEvent.documentKey._id;

  try {
    // If this is a "delete" event, delete the document in the other collection
    if (changeEvent.operationType === "delete") {
      try {
        path = `/remove_vector/${docId}`;
        console.log("Doc ID -", docId);

        let urlWithParams = externalURL + path;

        response = await axios.delete(urlWithParams, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': accessToken
          },
        });
        console.log("Data sent successfully:", response.data);
      } catch (error) {
        console.error("Error sending data:", error);
      }
    }

    // If this is an "insert" event, insert the document into the other collection
    else if (changeEvent.operationType === "insert" || changeEvent.operationType === "update" || changeEvent.operationType === "replace") {
      try {
        let document = changeEvent.fullDocument;
        path = `/add_vector/${docId}`;

        console.log("Org ID -", document.organizationId);
        console.log("Doc ID -", docId);
        
        let params = {
          orgID: document.organizationId
        };

        let query = Object.keys(params)
          .map(k => encodeURIComponent(k) + '=' + encodeURIComponent(params[k]))
          .join('&');

        let urlWithParams = externalURL + path + '?' + query;

        let api_spec;
        for (let id in document.idls) {
            api_spec = document.idls[id].content;
            break;
        }

        description = document.description;
        if (description === undefined || description === null || description === "") {
          description = document.summary;
        }

        let requestBody = {
          "api_name": document.name,
          "api_type": document.serviceType,
          "api_spec": api_spec,
          "description": description,
          "version": document.version,
          "api_uuid": document.serviceId
        };

        console.log("API UUID -", document.serviceId);

        response = await axios.post(urlWithParams, requestBody, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': accessToken
          },
        });
        console.log("Data sent successfully:", response.data);
        console.log("Data sent successfully:", response.status);

      } catch (error) {
        console.error("Error sending data:", error);
      }
    }
  } catch (err) {
    console.log("Error sinding data to spec populator service: ", err.message);
  }
};
