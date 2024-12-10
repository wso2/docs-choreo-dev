/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { VERY_SHORT_TIME } from "../../../commons/timeouts";
import { MARKETPLACE_SERVICES_API } from "../../../commons/urls";
import { Utils } from "../../../commons/utils";
import { TestIds } from "../../constants/TestIds";
import { ConsoleLeftMenu } from "../../ui-elements/left-menus/console-left-menu";


export enum MarketPlaceFilter {
    Internal = 'Internal',
    ThirdParty = 'Third Party',
    Organization = 'Organization',
    Public = 'Public',
    Project = 'Project'
}

class MarketPlace {
    private menu = new ConsoleLeftMenu();

    private filters = new Map<MarketPlaceFilter, string>([
        [MarketPlaceFilter.Internal, '[data-cyid="filtering-item-Internal-check-box"]'],
        [MarketPlaceFilter.ThirdParty, '[data-cyid="filtering-item-Third Party-check-box"]'],
        [MarketPlaceFilter.Organization, '[data-cyid="filtering-item-Organization-check-box"]'],
        [MarketPlaceFilter.Public, '[data-cyid="filtering-item-Public-check-box"]'],
        [MarketPlaceFilter.Project, '[data-cyid="filtering-item-Project-check-box"]']
    ]);

    find(searchString: string, expectedCount: number = 1) {
        this.menu.navigateToMarketplace();

        let searchUrl = MARKETPLACE_SERVICES_API + `${searchString}*`;

        cy.intercept({ method: "GET", url: searchUrl, times: 1}).as("marketPlaceTextSearch");

        cy.get(TestIds.marketPlaceSearch).within(() => {
            cy.get("input").eq(0)
            .should("be.visible")
            .clear()
            .wait(2000)
            .type(`${searchString}{enter}`);
        });

        cy.wait("@marketPlaceTextSearch", VERY_SHORT_TIME).then(() => {
            if (expectedCount == 0) {
                cy.contains("No services found").should("be.visible", VERY_SHORT_TIME);
            } else {
                cy.get(TestIds.ConnectionCard)
                    .contains(searchString)
                    .should("be.visible", VERY_SHORT_TIME)
                    .should('have.length', expectedCount);
            }
        });

        return cy.wrap({});
    }

    filterBy(searchFilters: MarketPlaceFilter[]) {
        this.menu.navigateToMarketplace();

        let checkList = this.cloneFiltersForSearch();

        this.determineSearchesForSpecifiedFilters(searchFilters, checkList).then((counter) => {

            cy.log(`Number of searches ${counter.numberOfSearches}`);

            if (counter.numberOfSearches > 0) {
                cy.intercept({ method: "GET", url: MARKETPLACE_SERVICES_API, times: counter.numberOfSearches}).as("marketPlaceFilterSearch");
            }
            
            for (const filter of searchFilters) {
                const filterSelector = this.filters.get(filter);

                if (filterSelector) {
                    Utils.checkIfUnchecked(filterSelector);
                } else {
                    throw new Error(`Filter selector for ${filter} is undefined`);
                }
            }

            for (const filter of checkList.values()) {
                Utils.unCheckIfChecked(filter);
            }

            if (counter.numberOfSearches > 0) {
                cy.wait("@marketPlaceFilterSearch", VERY_SHORT_TIME).wait(2000); // Wait extra 2 seconds to give time for page to load
            }
        });

        return cy.wrap({});
    }

    private cloneFiltersForSearch() : Map<MarketPlaceFilter, string> {
        return new Map(this.filters);
    }

    private determineSearchesForSpecifiedFilters(searchFilters: MarketPlaceFilter[], 
                                                    checkList: Map<MarketPlaceFilter, string>) : Cypress.Chainable<{ numberOfSearches: number }> {
        let searchCounter = { numberOfSearches: 0 };
        for (const filter of searchFilters) {
            const filterSelector = checkList.get(filter);
            if (filterSelector) {
                Utils.isChecked(filterSelector).then((isChecked) => {
                    if (!isChecked) { // This will need to be checked thereby triggering a marketplace search
                        searchCounter.numberOfSearches++;
                        cy.log(`Filter ${filter} to be checked, number of searches ${searchCounter}`);
                    }
                });
                checkList.delete(filter);
            } else {
                throw new Error(`Filter selector for ${filter} is undefined`);
            }
        }

        for (const filter of checkList.values()) {
            Utils.isChecked(filter).then((isChecked) => {
                if (isChecked) { // This will need to be unchecked thereby triggering a marketplace search
                    searchCounter.numberOfSearches++;
                    cy.log(`Filter ${filter} to be unchecked, number of searches ${searchCounter}`);
                }
            });
        }

        return cy.wrap(searchCounter);
   }
}

// Singleton instance of Choreo MarketPlace
export const marketplace = new MarketPlace();
