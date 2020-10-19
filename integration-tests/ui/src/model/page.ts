import { ChoreoLoginHook } from "../hooks/choreo-login-hook";
import { t, ClientFunction } from "testcafe";
import config from "../../testcafe-run-config.json";
import userConfig from "../../testcafe-user-config.json";
// import { isValidUser } from "../utils/login-utils";
import { isValidUser, getAccessToken, setTokenData } from "../utils/login-utils";
import { logger } from "../utils/logger";

const STORAGE_KEY = "PORTAL_STATE";
const backendRegexp = "https:\/\/"+config.backendHostName+"\/*"

class Page {
  loginHook = new ChoreoLoginHook(new RegExp(backendRegexp), {});
  localStorageSet = ClientFunction((key, value) =>
    localStorage.setItem(key, value)
  );

  constructor() {
    if (!isValidUser()) {
      throw new Error(
        "Please populate user details for logging in at testcafe-user-config.json"
      );
    }
  }

  async login() {
    logger.info("Starting login process ...")
    setTokenData(await getAccessToken());
    await t.addRequestHooks(this.loginHook);
    logger.info("Setting user data in local storage with Key : " + STORAGE_KEY +" , and  data : "+ JSON.stringify(config.user));
    await this.localStorageSet(
      STORAGE_KEY,
      JSON.stringify({
        userInfo: {
          isAuthenticated: true,
          isAuthInProgress: false,
          user: {
            name: userConfig.user.name,
            email: userConfig.user.email,
            token: userConfig.user.token,
            picURL: userConfig.user.picURL,
            orgs: userConfig.user.orgs,
          },
        },
      })
    );
    logger.info("local storage set successful!, navigating to URL : "+ config.testURL);
    await t.navigateTo(config.testURL);
  }
}

export default new Page();
