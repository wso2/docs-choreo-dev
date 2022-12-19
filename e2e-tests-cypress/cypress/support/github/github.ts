import { Utils } from "../console/utils";

export class GitHub {


    static initGitHubRepo(name: string, autoInit: boolean, isPrivate: boolean, gitignoreTemplate: string) {
        const requestURI = `${Cypress.env("ghUrl")}/orgs/${Cypress.env("ghOrg")}/repos`
        const headers = {
            Authorization: `token ${Cypress.env("gitPat")}`,
        };
        const payload = {
            name,
            "auto_init": autoInit,
            "private": isPrivate,
            "gitignore_template": gitignoreTemplate
        }

        Utils.sendPostRequest(requestURI, headers, payload).then(res => cy.log(res.body))
    }

    public static getGitHubRepoUrl(repoName) {
        return `https://github.com/${Cypress.env("ghOrg")}/${repoName}`
    }

    static mergePR(repoName, prNumber) {
        const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/pulls/${prNumber}/merge`
        const putRequest = { "commit_title": "Merge initial PR" }
        const headers = {
            Authorization: `token ${Cypress.env("gitPat")}`,
        };
        Utils.sendPutRequest(requestURI, headers, putRequest).then(res => cy.log(res.status.toString()))
    }
}