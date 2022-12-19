import { defineConfig } from 'cypress'

export default defineConfig({
  projectId: "$CYPRESS_PROJECT_ID",
  defaultCommandTimeout: 180000,
  pageLoadTimeout: 300000,
  responseTimeout: 300000,
  viewportHeight: 1000,
  viewportWidth: 1600,
  chromeWebSecurity: false,
  video: true,
  screenshotsFolder: 'cypress/screenshots/e2e-smoke',
  videosFolder: 'cypress/videos/e2e-smoke',
  videoUploadOnPasses: false,
  videoCompression: false,
  watchForFileChanges: false,
  e2e: {
    setupNodeEvents(on, config) {
      let apiName;
      let projectName;
      on('task', {

        setAPIName: (val) => {
          return (apiName = val);
        },

        getAPIName: () => {
          return apiName;
        },

        setChoreoProjectName: (val) => {
          return (projectName = val);
        },

        getChoreoProjectName: () => {
          return projectName;
        }
      });
      config.env.choreoIDPUsername = process.env.choreoIDPUsername;
      config.env.choreoIDPPassword = process.env.choreoIDPPassword;
      config.env.choreoOrgHandle = process.env.choreoOrgHandle;
      config.env.userName = process.env.userName;
      config.env.userEmail = process.env.userEmail;
      config.env.gitPat= process.env.gitPAT
      return config;

    },
    specPattern: 'cypress/e2e-smoke//./**/*.ts',
  },
})
