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
        return Utils.sendPostRequest(requestURI, headers, payload)
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
        return Utils.sendPutRequest(requestURI, headers, putRequest)
    }


    static createNewFile(repoName: string, path: string, filePath: string) {
        cy.readFile(filePath, 'base64').then(content => {
            const headers = {
                Authorization: `token ${Cypress.env("gitPat")}`,
            };
            const requestUrl = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/contents/${path}`
            const payload = { message: `Create file ${path}`, content }
            Utils.sendPutRequest(requestUrl, headers, payload)
        })


    }
}