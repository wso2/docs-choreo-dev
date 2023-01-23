import { Utils } from "../console/utils";

export class GitHub {

    static headers = {
        Authorization: `token ${Cypress.env("gitPat")}`,
    };
    static initGitHubRepo(name: string, autoInit: boolean, isPrivate: boolean, gitignoreTemplate: string) {
        const requestURI = `${Cypress.env("ghUrl")}/orgs/${Cypress.env("ghOrg")}/repos`
        const payload = {
            name,
            "auto_init": autoInit,
            "private": isPrivate,
            "gitignore_template": gitignoreTemplate
        }
        return Utils.sendPostRequest(requestURI, this.headers, payload)
    }

    public static getGitHubRepoUrl(repoName) {
        return `https://github.com/${Cypress.env("ghOrg")}/${repoName}`
    }

    static mergePR(repoName, prNumber) {
        const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/pulls/${prNumber}/merge`
        const putRequest = { "commit_title": "Merge initial PR" }
        return Utils.sendPutRequest(requestURI, this.headers, putRequest)
    }

    static deleteRepoContent(repoName: string) {
        const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/git/trees/main?recursive=1`
        Utils.sendGetRequest(requestURI, this.headers).then(res => {


            if (res.status == 200) {
                cy.log(JSON.stringify(res.body))
                const tree = res.body.tree as [];

                if (tree.length > 0) {
                    for (let i = 0; i < tree.length; i++) {
                        const { path, type, sha } = tree[i]
                        if (type !== "tree" && path !=="README.md") {
                            const deleteUrl = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/contents/${path}`
                            const payload = { message: `Delete ${path}`, sha }
                            Utils.sendDeleteRequest(deleteUrl, this.headers, payload)
                        }
                    }
                }
            }




        })
    }


    static createNewFile(repoName: string, path: string, filePath: string) {
        cy.readFile(filePath, 'base64').then(content => {
            const requestUrl = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/contents/${path}`
            const payload = { message: `Create file ${path}`, content }
            Utils.sendPutRequest(requestUrl, this.headers, payload)
        })


    }
}