import { Utils } from "../console/utils";

export class GitHub {


    static initGitHubRepo(name: string, autoInit: boolean, isPrivate: boolean, gitignoreTemplate: string) {
        const requestURI = `${Cypress.env("GH_URL")}/orgs/${Cypress.env("GH_ORG")}/repos`

        const headers = {
            Authorization: `token ${Cypress.env("gitPat")}`,
        };
        const payload = {
            name,
            "auto_init": autoInit,
            "private": isPrivate,
            "gitignore_template": gitignoreTemplate
        }

        Utils.sendPostRequest(requestURI,headers,payload)
    }
}