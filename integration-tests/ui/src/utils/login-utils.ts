import { ClientFunction } from "testcafe";
import * as config from "../../testcafe-run-config.json";
import * as userConfig from "../../testcafe-user-config.json";
import axios from "axios";
import qs from "qs";
import { logger } from "./logger";

export const getLocation = ClientFunction(() => document.location.href);

export const isValidUser = () => {
  return (
    userConfig.user.name !== "$USERNAME" &&
    userConfig.user.email !== "$EMAIL" &&
    userConfig.user.picURL !== "$AVATAR_URL" &&
    userConfig.user.orgs !== [] &&
    userConfig.idpUsername !== "$IDP_USERNAME" &&
    userConfig.idpPassword !== "$IDP_PASSWORD" &&
    userConfig.idpAuthHeader !== "$IDP_AUTH_HEADER" &&
    config.backendHostName !== "$BACKEND_HOSTNAME"
  );
};

export const getAccessToken = async () => {
  try {
    logger.info("Calling the endpoint " + config.idpURL + " to retrieve access token")
    const { data } = await axios.post(
      config.idpURL,
      qs.stringify({
        grant_type: "password",
        scope: "openid",
        username: userConfig.idpUsername,
        password: userConfig.idpPassword,
      }),
      {
        headers: {
          Authorization: userConfig.idpAuthHeader,
          ContentType: "application/x-www-form-urlencoded",
        },
      }
    );
    logger.info("Data returned from the IDP : " + JSON.stringify(data));
    return data;

  } catch (err) {
    logger.error("Error occurred while calling IDP endpoint " , err);
    throw new Error("Retrieving Access token failed : " + err);
  }
};

export const setTokenData = (data) => {
  const fragments = data["id_token"].split(".");
  userConfig.user.token = fragments[0] + "." + fragments[1];
  userConfig.user.cwatf = fragments[2];
  userConfig.user.cbearer = fragments[0] + "." + fragments[1];
};
