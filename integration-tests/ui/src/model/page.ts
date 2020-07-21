import { ChoreoLoginHook } from "../hooks/choreo-login-hook";
import { t, ClientFunction } from "testcafe";
import config from "../../testcafe-user-config.json";
import { isValidUser, getAccessToken, setTokenData } from "../utils/utils";

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
    setTokenData(await getAccessToken());
    await t.addRequestHooks(this.loginHook);
    await this.localStorageSet(
      STORAGE_KEY,
      JSON.stringify({
        userInfo: {
          isAuthenticated: true,
          isAuthInProgress: false,
          user: {
            name: config.user.name,
            email: config.user.email,
            token: config.user.token,
            picURL: config.user.picURL,
            orgs: config.user.orgs,
          },
        },
      })
    );
    await t.navigateTo(config.testURL);
  }
}

export default new Page();
