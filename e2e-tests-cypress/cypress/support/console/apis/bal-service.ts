
import { Utils } from "../../commons/utils";
import { NO_CONTENT_STATUS_CODE } from "./graphql";

export class BallerinaService {

    private static deleteConnector(pkg: any, header: any) {
        const { organization, name, version } = pkg;
        const url = `${Cypress.env("balRegistryURL")}/packages/${organization}/${name}/${version}?force=true`;
        Utils.sendDeleteRequest(url, header).then((res) => {
            if (res.status === NO_CONTENT_STATUS_CODE) {
                cy.log(`Successfully deleted Connector  ${name}`);
            } else {
                cy.log(`Could not delete connector: ${name}, status returned: ${res.status}`);
            }
        });
    }

    static deleteConnectors(token: string) {
        const { handle } = Cypress.env("userData");
        const headers = { Authorization: `Bearer ${token}` };
        Utils.sendGetRequest(`${Cypress.env("balRegistryURL")}/packages/${handle}`, headers).then((res) => {
            const packages = res.body as [];
            if (packages.length > 0) {
                packages.forEach((p) => this.deleteConnector(p, headers));
            }
        });
    }

}