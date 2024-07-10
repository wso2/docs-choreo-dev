import { defineConfig } from "cypress";
const { install } = require("@neuralegion/cypress-har-generator");

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
  videoCompression: false,
  watchForFileChanges: false,
  chromeWebSecurity: false,
  e2e: {
    setupNodeEvents(on, config) {
      const data = new Map<string, any>();
      on("task", {
        setData({ key, value }) {
          data.set(key as string, value);
          return value;
        },
        getData(key) {
          return data.get(key as string);
        },
      });
      require("cypress-fail-fast/plugin")(on, config);
      config.env.choreoIDPUsername = process.env.choreoIDPUsername;
      config.env.choreoIDPPassword = process.env.choreoIDPPassword;
      config.env.choreoOrgHandle = process.env.choreoOrgHandle;
      config.env.userName = process.env.userName;
      config.env.userEmail = process.env.userEmail;
      config.env.gitPAT = process.env.gitPAT;
      config.env.enableUnifiedMenu = process.env.enableUnifiedMenu;
      const port = 9222;
      install(on);
      return config;
    },
    testIsolation: false,
    specPattern: "cypress/e2e-*/**/*.ts",
    excludeSpecPattern: [
      'cypress/e2e-stable/devportal/1.4-devportal-selfsignup-when-auto-approval-enabled-test.ts',
      'cypress/e2e-stable/devportal/1.5-devportal-selfsignup-when-auto-approval-disabled-and-user-approved-test.ts',
      'cypress/e2e-stable/devportal/1.6-devportal-selfsignup-when-auto-approval-disabled-and-user-rejected-test.ts'
  ],
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
