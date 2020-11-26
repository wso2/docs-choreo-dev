import { RequestHook } from "testcafe";
import * as userConfig from "../../testcafe-user-config.json";

export class ChoreoLoginHook extends RequestHook {
  // @ts-ignore
  constructor(requestFilterRules, responseEventConfigureOpts) {
    super(requestFilterRules, responseEventConfigureOpts);
  }

  // @ts-ignore
  async onRequest(event) {
    if (event.requestOptions.url.includes("/linkersec/checklink")) {
      event.requestOptions.headers.cookie = "cwatf=" + userConfig.user.cwatf + "; " + event.requestOptions.headers.cookie;
    } else {
      event.requestOptions.headers.cookie = "cwatf=" + userConfig.user.cwatf + "; cbearer=" + userConfig.user.cbearer;
    }
    console.log("****request log****", event)
  }

  /*tslint:disable:no-empty */
  // @ts-ignore
  async onResponse(event) {
    // console.log(event);
  }
}
