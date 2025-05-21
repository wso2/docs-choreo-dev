/*
 * Usage: node cypress-open-stage.js
 *
 * Description: This is a convenience script to execute the cypress open command with the Choreo stage environment configs.
 * It uses the cypress.stage.env.json contents to set the environment variables for the cypress open command.
 */
const cypress = require('cypress');
const fs = require('fs');
const path = require('path');

// Choreo Stage env file
const jsonFilePath = path.join(__dirname, 'cypress.stage.env.json');

const username = process.env.CYPRESS_STAGE_USERNAME
const password = process.env.CYPRESS_STAGE_PASSWORD
const orgHandle = process.env.CYPRESS_STAGE_ORG_HANDLE

if (username && password && orgHandle) {
  process.env.cypress_enterpriseIDPUsername = username
  process.env.cypress_enterpriseIDPPassword = password
  process.env.choreoOrgHandle = orgHandle
}

try {
  const jsonData = fs.readFileSync(jsonFilePath, 'utf8');
  
  const data = JSON.parse(jsonData);

  cypress.open({
    env: data,
    });
  
} catch (error) {
  console.error('Error processing JSON file:', error.message);
}
