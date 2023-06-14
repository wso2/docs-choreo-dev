import axios from "axios";
import { defineConfig } from "cypress";

export default defineConfig({
  projectId: "$CYPRESS_PROJECT_ID",
  defaultCommandTimeout: 40000,
  pageLoadTimeout: 300000,
  responseTimeout: 300000,
  viewportHeight: 1000,
  viewportWidth: 1600,
  video: true,
  screenshotsFolder: "cypress/screenshots/e2e-smoke",
  videosFolder: "cypress/videos/e2e-smoke",
  videoUploadOnPasses: false,
  videoCompression: false,
  watchForFileChanges: false,
  chromeWebSecurity: false,
  e2e: {
    setupNodeEvents(on, config) {
      let apiName;
      let projectName;
      on("task", {
        setAPIName(val) {
          return (apiName = val);
        },

        getAPIName() {
          return apiName;
        },

        setChoreoProjectName(val) {
          return (projectName = val);
        },

        getChoreoProjectName() {
          return projectName;
        },

        sendRequest(request) {
          return axios.request(request).then(res => {
            console.log(JSON.stringify(res));
            
            return {
              body: res.data,
              status: res.status,
              config: res.config,
              headers: res.headers,
              statusText: res.statusText,
              request: res.request
            }
          }).catch(c => {
            console.log(JSON.stringify(c));
            
            return c
          })
        }
      });
      require("cypress-fail-fast/plugin")(on, config);
      config.env.choreoIDPUsername = process.env.choreoIDPUsername;
      config.env.choreoIDPPassword = process.env.choreoIDPPassword;
      config.env.choreoOrgHandle = process.env.choreoOrgHandle;
      config.env.userName = process.env.userName;
      config.env.userEmail = process.env.userEmail;
      config.env.gitPAT = process.env.gitPAT;
      config.env.enableUnifiedMenu = process.env.enableUnifiedMenu;
      return config;
    },
    testIsolation: false,
    specPattern: "cypress/e2e-smoke/**/*.ts",
  },
  env: {
    FAIL_FAST_STRATEGY: "spec",
    FAIL_FAST_ENABLED: true,
    FAIL_FAST_BAIL: 3,
    FAIL_FAST_PLUGIN: false,
  },
  retries: {
    // Configure retry attempts for `cypress run`
    // Default is 0
    runMode: 2,
    // Configure retry attempts for `cypress open`
    // Default is 0
    openMode: 0,
  },
});
