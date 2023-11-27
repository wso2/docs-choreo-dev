import { AUTH_HEADER } from "../../commons/http";
import { USER_STORE_MGT_URL } from "../../commons/urls";

export class UserstoreManagerService {
    static getUserstores(orgId: string) {
        const urls = `${USER_STORE_MGT_URL}/associations?orgId=${orgId}`;
        cy.log(urls);
        return cy.request({
            method: "GET",
            url: urls,
            headers: AUTH_HEADER()
        })
    }

    static deleteUserstore(userstoreId: string) {
        const urls = `${USER_STORE_MGT_URL}/${userstoreId}`;
        cy.log(urls);
        return cy.request({
            method: "DELETE",
            url: urls,
            headers: AUTH_HEADER()
        })
    }
}
