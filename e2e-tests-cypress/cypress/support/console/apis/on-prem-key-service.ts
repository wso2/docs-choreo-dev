

export class OnPremKeyService {
    static getOnPremKeys = (handle: string) => `${Cypress.env("newAppSvcURL")}/onprem-key-mgt/1.0.0/orgs/${handle}/keys`
    static deleteOnPremKey = (handle: string, keyHandle: string) => `${Cypress.env("newAppSvcURL")}/onprem-key-mgt/1.0.0/orgs/${handle}/keys/${keyHandle}/revoke`
}