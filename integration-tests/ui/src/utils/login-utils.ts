import { ClientFunction } from "testcafe";
import * as config from "../../testcafe-user-config.json";
import axios from "axios";
import qs from "qs";

export const getLocation = ClientFunction(() => document.location.href);

export const isValidUser = () => {
  return (
    config.user.name !== "$USERNAME" &&
    config.user.email !== "$EMAIL" &&
    config.user.picURL !== "$AVATAR_URL" &&
    config.user.orgs !== [] &&
    config.idpUsername !== "$IDP_USERNAME" &&
    config.idpPassword !== "$IDP_PASSWORD" &&
    config.idpAuthHeader !== "$IDP_AUTH_HEADER" &&
    config.backendHostName !== "$BACKEND_HOSTNAME"
  );
};

export const getAccessToken = async () => {
  try {
    const { data } = await axios.post(
      config.idpURL,
      qs.stringify({
        grant_type: "password",
        scope: "openid",
        username: config.idpUsername,
        password: config.idpPassword,
      }),
      {
        headers: {
          Authorization: config.idpAuthHeader,
          ContentType: "application/x-www-form-urlencoded",
        },
      }
    );
    return data;
  } catch (err) {
    throw new Error("Retrieving Access token failed : " + err);
  }
};

export const setTokenData = (data) => {
  const fragments = data["id_token"].split(".");
  config.user.token = fragments[0] + "." + fragments[1];
  config.user.cwatf = fragments[2];
  config.user.cbearer = fragments[0] + "." + fragments[1];
};
