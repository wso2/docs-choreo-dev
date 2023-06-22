import { Utils } from "../commons/utils";


export class GitHub {

    static headers = {
        Authorization: `token ${Cypress.env("gitPAT")}`,
    };


    static getPRs(repoName: string) {
        const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/pulls`
        return Utils.sendGetRequest(requestURI, this.headers).then(res => {
            return res.body as { id: number, url: string }[]
        })
    }


    static getPR(repoName: string, prNumber: number) {
        const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/pulls/${prNumber}`
        return Utils.sendGetRequest(requestURI, this.headers)
    }


    static mergePR(repoName, prNumber) {
        const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/pulls/${prNumber}/merge`
        const putRequest = { "commit_title": "Merge initial PR" }

        this.getPR(repoName, prNumber).then(res => {
            if (res.status != 200) {
                this.getPR(repoName, prNumber)
            } else {
                cy.log(requestURI)
                Utils.sendPutRequest(requestURI, this.headers, putRequest).then((resp) => expect(resp.status).to.be.eq(200))
            }
        })
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

    static deleteWebhooks(repoName: string) {
        const requestURI = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/hooks`
        Utils.sendGetRequest(requestURI, this.headers).then(res => {
            if (res.status === 200) {
                const wh = res.body as { id: number, created_at: string }[]
                wh.forEach(w => {
                    const hourDiff = Date.now() - 3600000;
                    const createdTime = Date.parse(w.created_at)
                    if (createdTime < hourDiff) {
                        const deleteRequest = `${Cypress.env("ghUrl")}/repos/${Cypress.env("ghOrg")}/${repoName}/hooks/${w.id}`
                        Utils.sendDeleteRequest(deleteRequest, this.headers)
                    }
                })
            }

        })

    }








}