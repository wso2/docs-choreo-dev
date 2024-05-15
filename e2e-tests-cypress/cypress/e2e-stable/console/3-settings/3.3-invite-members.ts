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

import { console } from "../../../support/console/console";
import { Utils } from "../../../support/commons/utils";
import { USER_ORGS_URL } from "../../../support/commons/urls";
import { login } from "../../../support/console/entities/login/login";
import { OK } from "../../../support/commons/http";

function deleteInvitation(email: string) {
  const handle = login.getOrgHandle();
  const token = login.getAccessToken();

  const headers = {
    authorization: `Bearer ${token}`,
  };
  const deletePendingInvitation = `${USER_ORGS_URL}/${handle}/invitations?email=${email}`;
  const getUsers = `${USER_ORGS_URL}/${handle}/users`;

  Utils.sendGetRequest(getUsers, headers).then((res) => {
    const list = res.body.list as [];
    const user = list.find((u) => u["email"] === email);
    cy.log(JSON.stringify(user));

    if (user) {
      const { idpId } = user;
      const deleteUserRequest = `${USER_ORGS_URL}/${handle}/users/${idpId}`;

      Utils.sendDeleteRequest(deleteUserRequest, headers).then((res) => {
        if (res.status === OK) {
          cy.log("Deleted Invited User");
        } else {
          cy.log("User Has Not Invited Or Error");
        }
      });
    }
    Utils.sendDeleteRequest(deletePendingInvitation, headers).then((res) => {
      if (res.status === OK) {
        cy.log("Deleted Invited User");
      } else {
        cy.log("User Has Not Invited Or Error");
      }
    });
  });
}

function acceptEmailInviteToOrg(timestamp: string, retryCount = 0) {
  const headerString = btoa(
    `${Utils.MAIL_READER_CLIENT_ID}:${Utils.MAIL_READER_CLIENT_SECRET}`
  );
  cy.wait(5000);
  if (retryCount > 5) {
    return;
  }
  Utils.sendPostRequest(
    Utils.MAIL_READER_TOKEN_URL,
    { Authorization: `Basic ${headerString}` },
    { grant_type: "client_credentials" }
  ).then((res) => {
    const accessToken = res.body.access_token;
    Utils.sendGetRequest(
      Utils.MAIL_READER_SVC_URL +
        timestamp +
        "&senderEmail=" +
        Utils.ASGARDEO_MAIL_SENDER,
      {
        Authorization: `Bearer ${accessToken}`,
      }
    ).then((res) => {
      if (res.status == 200 && res.body != "") {
        const rawMailContent = res.body;
        const decodedMail = window.atob(rawMailContent);

        const asgardeocUrlRegex =
          /https:\/\/[a-zA-Z0-9.-]+\/invite-user-register\?email=[^'"]+/im;
        const match = asgardeocUrlRegex.exec(decodedMail);
        const asgardeoAcceptUrl = match
          ? match[0].replace(/&amp;/g, "&")
          : null;

        if (asgardeoAcceptUrl) {
          cy.window().then((win) => {
            win.open(asgardeoAcceptUrl, "_blank");
            cy.wait(2000);
            cy.window().then((newWin) => {
              cy.wrap(newWin.document.body).should(
                "not.contain",
                "Registration failed"
              );
            });
          });
        } else {
          cy.log("Asgardeo redirect URL not found");
        }
      } else {
        cy.log(`Error while reading email: ${res.status}`);
        retryCount++;
        acceptEmailInviteToOrg(timestamp, retryCount);
      }
    });
  });
}

describe("Invite members", () => {
  const timestamp = Math.floor((+new Date() - 100000) / 1000).toString();
  const INVITATION_EMAIL = Cypress.env("invitationUserEmail");

  before(() => {
    console.login();
  });
  after(() => {
    console.logout();
  });

  it("Delete existing invitation", () => {
    deleteInvitation(INVITATION_EMAIL);
  });

  it("Invite a member to users org", () => {
    console.inviteMember(INVITATION_EMAIL, ["API Publisher"]);
  });

  it("Accept the invitation and open Register page", () => {
    acceptEmailInviteToOrg(timestamp);
  });
});
