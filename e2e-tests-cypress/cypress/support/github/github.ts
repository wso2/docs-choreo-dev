import { Utils } from "../console/utils";

export class GitHub {

    static headers = {
        Authorization: `token ${Cypress.env("gitPAT")}`,
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
        const env = Cypress.env("branch").replace("-ci", "");
        const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/git/trees/main?recursive=1`
        Utils.sendGetRequest(requestURI, this.headers).then(res => {
            if (res.status == 200) {
                const tree = res.body.tree as [];

                if (tree.length > 0) {
                    for (let i = 0; i < tree.length; i++) {
                        const { path, type, sha } = tree[i]
                        const subPath = path as string
                        if (type !== "tree" && !subPath.includes('README.md') && subPath.includes(`${env}`)) {
                            const deleteUrl = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/contents/${subPath}`
                            const payload = { message: `Delete ${subPath}`, sha }
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

    static mergeNewCode(repoName: string, repoPath: string, filePath: string) {
        cy.readFile(filePath, 'base64').then(content => {
            const requestUrl = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/contents/${repoPath}`
            Utils.sendGetRequest(requestUrl, this.headers).then(res => {
                const { sha } = res.body.content
                const payload = {
                    message: `updateed ${repoPath}`,
                    content,
                    sha
                }

                Utils.sendPutRequest(requestUrl, this.headers, payload).then(res => {
                    expect(res.status).to.be.eq(200)
                })
            })
        })
    }



    // public static Response mergeNewCode(String repoName, String path, String commitMessage, String content) throws IOException {

    //     String requestUrl = GH_URL + "/repos/" + GH_ORG + "/" + repoName + "/contents/" + path;
    //     Response response = HttpClientUtil.httpGET(requestUrl, AUTH_HEADER, "");
    //     JsonObject jsonObject = new JsonParser().parse(response.getRes()).getAsJsonObject();
    //     String serviceBalSha = jsonObject.get("sha").getAsString();
    //     String request = "{\n" +
    //     "    \"message\":" + "\"" + commitMessage + "\"" + " ,\n" +
    //     "    \"content\":" + "\"" + content + "\"" + ",\n" +
    //     "    \"sha\":" + "\"" + serviceBalSha + "\"" + "\n}";
    // return HttpClientUtil.httpPUT(requestUrl, request, AUTH_HEADER, "");
    //

}