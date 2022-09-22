import { defineConfig } from 'cypress'

export default defineConfig({
  projectId:"2miy5a",
  defaultCommandTimeout: 180000,
  pageLoadTimeout: 300000,
  responseTimeout: 300000,
  viewportHeight: 1080,
  viewportWidth: 1920,
  chromeWebSecurity: false,
  video: true,
  screenshotsFolder: 'cypress/screenshots/e2e-smoke',
  videosFolder: 'cypress/videos/e2e-smoke',
  videoUploadOnPasses: false,
  videoCompression: false,
  watchForFileChanges: false,
  scrollBehavior: false,
  e2e: {
    // We've imported your old cypress plugins here.
    // You may want to clean this up later by importing these.
    setupNodeEvents(on, config) {
      let apiName;
      on('task', {

        setAPIName: (val) => {
          return (apiName = val);
        },

        getAPIName: () => {
          return apiName;
        }
      });
      config.env.choreoIDPUsername = process.env.choreoIDPUsername;
      config.env.choreoIDPPassword = process.env.choreoIDPPassword;
      config.env.choreoOrgHandle = process.env.choreoOrgHandle;
      config.env.userName = process.env.userName;
      config.env.userEmail = process.env.userEmail;
      return config;
      // return require('./cypress/plugins/index.js')(on, config)
    },
    specPattern: 'cypress/e2e-smoke//./**/*.ts',
  },
})
