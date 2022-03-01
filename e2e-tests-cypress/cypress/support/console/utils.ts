/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

export class Utils {
  static projectNamePrefix = "e2eproject";

  static keyNamePrefix = "e2eOnPremkey";
  static APP_SVC_URL = Cypress.env("appSvcURL");
  static ORG_NAME = Cypress.env("choreoOrgHandle");
  static MAIL_READER_SVC_URL = Cypress.env("mailReaderSvcURL");
  static MAIL_READER_CLIENT_ID = Cypress.env("mailReaderClientId");
  static MAIL_READER_CLIENT_SECRET = Cypress.env("mailReaderClientSecret");
  static MAIL_READER_TOKEN_URL = Cypress.env("mailReaderTokenURL");

  /**
   * Create name for app.
   *
   * @returns true name for a new app
   */
  static generateProjectName() {
    return this.projectNamePrefix + Date.now();
  }

  /**
   * Create name for on-prem key.
   *
   * @returns true name for a new on-prem key
   */
  static generateKeyName(name: string) {
    return this.keyNamePrefix + Date.now() + name;
  }

  static getInvitationId(token: string, timestamp: string) {
    const headerString = btoa(`${Utils.MAIL_READER_CLIENT_ID}:${Utils.MAIL_READER_CLIENT_SECRET}`);
    this.sendPostRequest("POST", Utils.MAIL_READER_TOKEN_URL,
        { Authorization: `Basic ${headerString}`}, { grant_type: "client_credentials"})
        .then((res) => {
          const accessToken = res.body.access_token;
          this.sendGetRequest("GET", Utils.MAIL_READER_SVC_URL + timestamp,
              { Authorization: `Bearer ${accessToken}` })
              .then((res) => {
                const rawMailContent = res.body;
                const decodedMail = atob(rawMailContent);

                const socRegEx = /^<!DOCTYPE html PUBLIC /im;
                const bodyPos = decodedMail.indexOf(socRegEx.exec(decodedMail) as unknown as string);
                let bodyLines = decodedMail.substring(bodyPos);
                bodyLines = bodyLines.replace(/\r?\n?[^\r\n]*$/, "");
                bodyLines = bodyLines.replace(/\r?\n?[^\r\n]*$/, "");
                const invitationId = /[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}/.exec(bodyLines)[0];

                const header = {
                  Authorization: `Bearer ${token}`,
                  "content-type": "application/json",
                };
                this.sendGetRequest("POST", `${Utils.APP_SVC_URL}/v2/orgs/${Utils.ORG_NAME}/invitations/${invitationId}`,
                    header);
              });
        });
  }

  static saveComponentURL(testKey){
    cy.url().then((url) => {
      Cypress.env(`${testKey}_componentURL`, url);
    });
  }

  static saveProjectData(testKey){
    cy.intercept(Cypress.env("appSvcURL") + "/graphql").as("proj_create");
    cy.wait('@proj_create',{timeout:180000}).then(intercept=>{

      const token =  JSON.stringify(intercept.request.headers["authorization"]).split(" ")[1].replace(/"/g,'').replace(/'/g,'')
      const {id,projectId} = intercept.response.body.data.createComponent
      cy.log(JSON.stringify(token))
      cy.log(JSON.stringify(id))
      cy.log(JSON.stringify(projectId))
      Cypress.env(`${testKey}_component_id`,id)
      Cypress.env(`${testKey}_projectId`,projectId)
      Cypress.env(`${testKey}_apim_token`,token)
    })
  }

  static sendPostRequest(method: string, url: string, headers, body) {
    const request = {
      method,
      url,
      headers,
      body
    };
    return cy.request(request).then((res) => {
      return cy.wrap({ body: res.body, status: res.status });
    });
  }

  static sendGetRequest(method: string, url: string, headers:any={}) {
    const request = {
      method,
      url,
      headers
    };
    return cy.request(request).then((res) => {
      return cy.wrap({ body: res.body, status: res.status });
    });
  }
}
