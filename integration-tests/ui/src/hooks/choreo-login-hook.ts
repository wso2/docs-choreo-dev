import { RequestHook } from "testcafe";
import * as config from "../../testcafe-user-config.json";

export class ChoreoLoginHook extends RequestHook {
  // @ts-ignore
  constructor(requestFilterRules, responseEventConfigureOpts) {
    super(requestFilterRules, responseEventConfigureOpts);
  }

  // @ts-ignore
  async onRequest(event) {
    event.requestOptions.headers.cookie = "cwatf=" + config.user.cwatf;
    // console.log(event)
  }

  /*tslint:disable:no-empty */
  // @ts-ignore
  async onResponse(event) {
    // console.log(event);
  }
}
