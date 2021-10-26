/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

import { HTTPMethod } from "./component/enums/http-method-enum"

export class CurlComponent {

    static generateRequest(httpMethod: HTTPMethod, pathParams: string) {
        cy.get('[data-testid="cUrl-btn"]').click()
        cy.get('#method').click()
        let method = `li[data-value="${httpMethod}"]`
        cy.get(method).click()
        if (pathParams != "" && pathParams != null) {
            cy.get('#path-id').clear().type(pathParams)
        }
        cy.get('[data-testid="curl-command"] div input').invoke('attr', 'value').then(val => {
            this.makeCurlRequest(val, 4)
        })
        cy.get('body').type('{esc}')
    }


    static makeCurlRequest(curlReques: string, frequency: number) {
        let arr = curlReques.split(' ')
        let options = {
            "method": arr[6].trim(),
            "url": arr[1].replace(/"/g, '').trim(),
            "headers": {
                "API-Key": arr[4].replace(/'/g, '').trim()
            }
        }
        for (let i = 0; i < frequency; i++) {
            cy.request(options)
            cy.wait(500)
        }
    }
}



