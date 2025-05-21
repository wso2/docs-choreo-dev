import { VERY_SHORT_TIME } from "../commons/timeouts";
import { Utils } from "../commons/utils";

export class GitHub {
  static env: string = Cypress.env("branch").replace("-ci", "");
  static url: string = Cypress.env("ghUrl");
  static org: string = Cypress.env("ghOrg");

  static headers = {
    Authorization: `token ${Cypress.env("gitPAT")}`,
  };

  static getPRs(repoName: string) {
    const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env(
      "ghOrg"
    )}/${repoName}/pulls`;
    return Utils.sendGetRequest(requestURI, this.headers).then((res) => {
      return res.body as { id: number; url: string }[];
    });
  }

  static getPR(repoName: string, prNumber: number) {
    const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env(
      "ghOrg"
    )}/${repoName}/pulls/${prNumber}`;
    return Utils.sendGetRequest(requestURI, this.headers);
  }

  static mergePR(repoName, prNumber, count = 0) {
    const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env(
      "ghOrg"
    )}/${repoName}/pulls/${prNumber}/merge`;
    const putRequest = { commit_title: "Merge initial PR" };

    this.getPR(repoName, prNumber).then((res) => {
      if (res.status == 200) {
        cy.log(requestURI);
        Utils.sendPutRequest(requestURI, this.headers, putRequest).then(
          (resp) => {
            if (resp.status == 200) {
              cy.log("Merge successful");
            } else {
              if (count < 10) {
                cy.wait(10000);
                this.mergePR(repoName, prNumber, count + 1);
              } else {
                cy.log("Merge failed");
              }
            }
          }
        );
      }
    });
  }

  static syncForkWithUpstream(repoName: string, branch: string) {
    const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env(
      "ghOrg"
    )}/${repoName}/merge-upstream`;

    Utils.sendPostRequest(requestURI, this.headers, {
      branch: `${branch}`,
    }).then((resp) => {
      if (resp.status == 200) {
        cy.log("Sync successful");
      } else {
        cy.log("Sync failed with status code: " + resp.status);
      }
    });
  }

  static deleteRepoContent(repoName: string) {
    const requestURI = `${this.url}/repos/${this.org}/${repoName}/contents/${this.env}`;
    Utils.sendGetRequest(requestURI, this.headers).then((res) => {
      if (res.status == 200) {
        const files = res.body as [];

        if (files.length > 0) {
          for (let i = 0; i < files.length; i++) {
            const { name, type, sha } = files[i];
            if (name !== "README.md") {
              if (type === "dir") {
                this.deleteSubFolders(repoName, name);
              } else {
                const deleteUrl = `${this.url}/repos/${this.org}/${repoName}/contents/${this.env}/${name}`;
                const payload = { message: `Delete ${name}`, sha };
                Utils.sendDeleteRequest(deleteUrl, this.headers, payload);
                cy.wait(500); // Sleep for 500ms to avoid rate limiting
              }
            }
          }
        }
      }
    });
  }

  private static deleteSubFolders(repoName: string, folderName: string) {
    const requestURI = `${this.url}/repos/${this.org}/${repoName}/contents/${this.env}/${folderName}`;
    Utils.sendGetRequest(requestURI, this.headers).then((res) => {
      if (res.status == 200) {
        const files = res.body as [];

        if (files.length > 0) {
          for (let i = 0; i < files.length; i++) {
            const { name, sha } = files[i];

            const deleteUrl = `${this.url}/repos/${this.org}/${repoName}/contents/${this.env}/${folderName}/${name}`;
            const payload = { message: `Delete ${name}`, sha };
            Utils.sendDeleteRequest(deleteUrl, this.headers, payload);
            cy.wait(500); // Sleep for 500ms to avoid rate limiting
          }
        }
      }
    });
  }

  static deleteWebhooks(repoName: string) {
    const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env(
      "ghOrg"
    )}/${repoName}/hooks`;
    Utils.sendGetRequest(requestURI, this.headers).then((res) => {
      if (res.status === 200) {
        const wh = res.body as { id: number; created_at: string }[];
        wh.forEach((w) => {
          const hourDiff = Date.now() - 3600000;
          const createdTime = Date.parse(w.created_at);
          if (createdTime < hourDiff) {
            const deleteRequest = `${Cypress.env("ghUrl")}/repos/${Cypress.env(
              "ghOrg"
            )}/${repoName}/hooks/${w.id}`;
            Utils.sendDeleteRequest(deleteRequest, this.headers);
          }
        });
      }
    });
  }
}
