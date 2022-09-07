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

import { GraphQL } from "../../apis/graphql"
import { Utils } from "../../utils"


export class GreetingSample {
  static selectSample(service: string, isEPLogin: boolean = false) {
    Utils.setBrowserCookie(isEPLogin)
    cy.contains("Get started with a template").should("be.visible")
    cy.get("button>span>p").each($p => {
      cy.log($p.text())
      if ($p.text().trim() === 'View all') {
        cy.wrap($p).click()
      }
    })
    cy.get('div[class*=" MuiDialog-scrollPaper"]>div>div>div>div>div>div>div>button').click()
    cy.get("div>div[data-cyid]>div>div>h3").should("have.length.greaterThan", 2)
    cy.get('div[role="none presentation"]>div>div>div>div>div>div>div>div>div>div>input').should('be.visible').type(`${service}{enter}`)
    cy.get(`[data-cyid=${service.toLowerCase().replace(" ", "_")}]`).should("be.visible")
    cy.get(`[data-cyid=${service.toLowerCase().replace(" ", "_")}]`).eq(0).realHover().wait(2000)
    Utils.setBrowserCookie(isEPLogin)
    cy.get(`[data-cyid=${service.toLowerCase().replace(" ", "_")}]`).eq(0).realClick()
    cy.intercept("https://apis.preview-dv.choreo.dev/projects/1.0.0/graphql").as('gql')

    cy.wait('@gql').then(int => {
      const { id, projectId } = int.response.body.data.createComponent
      cy.log(JSON.stringify(int.response.body.data.createComponent))
      const query = { query: `mutation {startCodeServer(orgId: 869,orgHandler: "dasunatwso2com",projectId: "${projectId}",componentId: "${id}",fidp: "choreoe2etest")}` }
    cy.log(query)
    GraphQL.callGraphQL(Cypress.env("apim_token"),query).then(res=>{
      cy.log(JSON.stringify(res.body))
    })
    })
   // cy.pause()

    Utils.setBrowserCookie(isEPLogin)
  }
}
