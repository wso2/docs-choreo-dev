var transport = require('./libs/transport.js');
var jwt = require('./libs/jwt.js');
var schema = require('./libs/gql-schema.js');
let hostname;

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
  const jwtPayload = jwt.getJwtPayload(token);
  const orgId = args[2];
  const orgHandler = jwtPayload.organization.handle;
  const componentName = args[3];
  findComponents(token, orgId, orgHandler, componentName);
} else {
  printUsage();
}

async function findComponents(token, orgId, orgHandler, componentName) {

    let body = schema.buildGetProjectsQuery(orgId);

    await transport.sendGraphQLPost(token, hostname, body, function(response){
      // For debugging
      //console.log(`\ngetProject reponse: ${JSON.stringify(response)}\n`);

      response.data.projects.forEach(project => {
        let body = schema.buildGetComponentsQuery(orgHandler, project.id);

        transport.sendGraphQLPost(token, hostname, body, function(response) {
        // For debugging
        //console.log(`getComponents reponse: ${JSON.stringify(response)}`);

          if (response.hasOwnProperty("data") && response.data.components.length > 0) {
            response.data.components.forEach(component => {
              if (component.displayName.search(componentName) > -1) {
                  console.log(`Found component: ${component.displayName}`);
                  console.log(`Project name: ${project.name}`);
              }
            });
          }
        });
      });
    })
  }

function printUsage() {
    console.log("\n");
    console.log("usage: node find-component.js {dev|stage|prod} <access_token> <orgId> <component_name>");
    process.exit(1);
}