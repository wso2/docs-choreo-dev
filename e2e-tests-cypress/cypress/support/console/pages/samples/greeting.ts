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

import { Utils } from "../../utils"


export class GreetingSample {
  static selectSample(service: string, isEPLogin: boolean = false) {
    const sampleService =`[data-cyid=${service.toLowerCase().replace(" ", "_")}]`
    Utils.setBrowserCookie(isEPLogin)
    cy.contains("Get started with a template").should("be.visible")
    cy.get("button>span>p").each($p => {
      cy.log($p.text())
      if ($p.text().trim() === 'View all') {
        cy.wrap($p).click()
      }
    })
    cy.contains('View All Samples').focus().click()
    cy.get('[data-cyid="greeting_service"]').should("be.visible")
    cy.get('[placeholder="Search by Samples"]').should('be.visible').type(`${service}{enter}`)

    cy.get(`${sampleService}`).should("be.visible")
    cy.get(`${sampleService}`).eq(0).realHover().wait(2000)
    Utils.setBrowserCookie(isEPLogin)
    cy.get(`${sampleService}`).eq(0).realClick()


    Utils.setBrowserCookie(isEPLogin)
  }
}
