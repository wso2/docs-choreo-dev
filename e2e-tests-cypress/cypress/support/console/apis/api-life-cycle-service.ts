import { Utils } from "../../commons/utils";
import { AUTH_HEADER } from "../../commons/http";


export class APILifeCycleService {




    static deprecateAPI(apiId: string) {
        const { uuid } = Cypress.env("userData");
        const deprecateRequest = `${Cypress.env("apimSvcURL")}/api/am/publisher/v2/apis/change-lifecycle?organizationId=${uuid}&apiId=${apiId}&action=Deprecate`;
        Utils.sendPostRequest(deprecateRequest, AUTH_HEADER(), {});
        cy.log(`Deprecated API ${apiId}`)
    }

    static retireAPI(apiId: string) {
        const { uuid } = Cypress.env("userData");
        const retireRequest = `${Cypress.env("apimSvcURL")}/api/am/publisher/v2/apis/change-lifecycle?organizationId=${uuid}&apiId=${apiId}&action=Retire`;
        Utils.sendPostRequest(retireRequest, AUTH_HEADER(), {});
        cy.log(`Retired API ${apiId}`)
    }

}