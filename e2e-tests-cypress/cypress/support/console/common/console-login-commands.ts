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

import qs from 'qs';
import { APP_SVC_URL } from '../../common/constants';
import { getSelectedOrgHandle } from '../../common/utils';

Cypress.on('uncaught:exception', (err, runnable) => {
    console.log(err);
    return false;
})

Cypress.Commands.add('userLoginWithGmail', () => {
    const username = Cypress.env('username')
    const password = Cypress.env('password')
    const loginUrl = Cypress.env('loginURL')
    const cookieName = Cypress.env('choreoCookies')
    const socialLoginOptions = {
        username: Cypress.env('username'),
        password: Cypress.env('password'),
        loginUrl: Cypress.env('loginURL'),
        headless: false,
        logs: true,
        loginSelector: '[id="google-sign-in"]',
        postLoginSelector: '[id="current-user"]',
        popupDelay: 2000,
        cookieDelay: 2000,
        args: ['--no-sandbox'],
        isPopup: false,
        getAllBrowserCookies: true
    }

    cy.clearCookies()
    cy.task('GoogleSocialLogin', socialLoginOptions).then(({ cookies, lsd, ssd }) => {

        cookies.map((cookie) => {
            cy.setCookie(cookie.name, cookie.value, {
                domain: cookie.domain,
                expiry: cookie.expires,
                httpOnly: cookie.httpOnly,
                path: cookie.path,
                secure: cookie.secure
            })

            Cypress.Cookies.defaults({
                preserve: cookie.name
            })
        })

        cy.window().then(window => {
            Object.keys(ssd).forEach(key => window.sessionStorage.setItem(key, ssd[key]))
            Object.keys(lsd).forEach(key => window.localStorage.setItem(key, lsd[key]))
        })

        cy.hideWelcomeMessage();
        cy.log('login successful. Visiting to choreo')
        cy.visit('/')

    })
}),

Cypress.Commands.add('userLoginWithGithub', () => {
    const username = Cypress.env('username')
    const password = Cypress.env('password')
    const loginUrl = Cypress.env('loginURL')
    const cookieName = Cypress.env('choreoCookies')
    const socialLoginOptions = {
        username: Cypress.env('username'),
        password: Cypress.env('password'),
        loginUrl: Cypress.env('loginURL'),
        headless: false,
        logs: true,
        loginSelector: '[id="github-sign-in"]',
        postLoginSelector: '[id="current-user"]',
        popupDelay: 2000,
        cookieDelay: 2000,
        args: ['--no-sandbox'],
        isPopup: false,
        getAllBrowserCookies: true
    }

    cy.clearCookies()
    return cy.task('GitHubSocialLogin', socialLoginOptions).then(({ cookies, lsd, ssd }) => {
        cy.log('cokies: ', cookies)
        // cy.clearCookies()

        const cookie = cookies.filter(cookie => cookie.name === cookieName).pop()
        if (cookie) {
            cy.setCookie(cookie.name, cookie.value, {
                domain: cookie.domain,
                expiry: cookie.expires,
                httpOnly: cookie.httpOnly,
                path: cookie.path,
                secure: cookie.secure
            })

            Cypress.Cookies.defaults({
                preserve: cookieName
            })
        }

        cy.window().then(window => {
            Object.keys(ssd).forEach(key => window.sessionStorage.setItem(key, ssd[key]))
            Object.keys(lsd).forEach(key => window.localStorage.setItem(key, lsd[key]))
        })

        cy.log('login successful. Visiting to choreo');
        cy.hideWelcomeMessage();
        cy.visit('/');
    })
}),

Cypress.Commands.add('userLogout', () => {
    cy.log("Logout from Choreo");
    cy.get('[id="current-user"]').click({ force: true });
    cy.wait(2000);
    cy.contains('Logout').click({ force: true });
    cy.url().should('include', '/login');
    cy.clearCookies();
    cy.clearLocalStorage();
    cy.window().then((win) => {
        win.sessionStorage.clear()
    });
    cy.log("logout successfully");
})

Cypress.Commands.add('consoleUserLogin', () => {
    cy.log('Initiating login');
    cy.visit(Cypress.env('baseUrl'));
    const testURL = Cypress.env('testURL');
    const idpURL = Cypress.env('idpURL');
    const idpUsername = Cypress.env('idpUsername');
    const idpPassword = Cypress.env('idpPassword');
    const idpAuthHeader = Cypress.env('idpAuthHeader');
    try {
        cy.request({
            method: 'POST',
            url: idpURL,
            form: true,
            body: qs.stringify({
                grant_type: "password",
                scope: "openid",
                username: idpUsername,
                password: idpPassword,
            }),
            headers: {
                Authorization: idpAuthHeader,
            }
        }).then((response) => {
            const data = response["body"];
            cy.log('Data received from the IDP');
            const fragments = data["id_token"].split(".");
            const token = fragments[0] + "." + fragments[1];
            const cwatf = fragments[2];
            const cbearer = fragments[0] + "." + fragments[1];
            const accessTokenSegments = data["access_token"].split(".");
            const accessToken = accessTokenSegments[0] + "." + accessTokenSegments[1];
            cy.log('Starting login process...');

            Cypress.Cookies.defaults({
                preserve: ['cwatf', 'cbearer', 'id_token', 'token']
            });

            let apimTokenResponse: ApimTokenResponse;
            cy.request({
                method: 'POST',
                url: APP_SVC_URL + "/auth/apim-token",
                body: {
                    client_id: "choreoconsole",
                    scope: "apim:api_manage apim:subscription_manage apim:tier_manage apim:admin"
                },
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                    Cookie: `cwatf=${accessTokenSegments[2]}`
                }
            }).then((response) => {
                apimTokenResponse = response.body as ApimTokenResponse;
            });

            cy.request({
                method: 'GET',
                url: APP_SVC_URL + "/orgs",
                headers: {
                    Authorization: `Bearer ${token}`,
                    Cookie: `cwatf=${cwatf}`
                }
            }).then((response) => {
                const orgs = response.body as Organization[];

                const jwtPayload = JSON.parse(atob(fragments[1]));
                const idpId = jwtPayload.sub;
                const user: User = {
                    id: orgs[0].id.toString(),
                    name: jwtPayload.name,
                    uuid: idpId,
                    email: jwtPayload.email,
                    token: token,
                    apimToken: apimTokenResponse.access_token,
                    picURL: jwtPayload.avatar_url,
                    orgs: orgs,
                    createdAt: new Date(jwtPayload.iat * 1000),
                    expiredAt: new Date(jwtPayload.exp * 1000),
                };

                const selectedOrgHandleEnv = Cypress.env('selectedOrgHandle');
                if (selectedOrgHandleEnv) {
                    cy.wrap(orgs.map(org => org.handle)).should("include", selectedOrgHandleEnv);
                }
                const selectedOrgHandle = getSelectedOrgHandle(user);
                cy.log("Selected org handle for running tests: " + selectedOrgHandle);

                cy.request({
                    method: 'GET',
                    url: `${APP_SVC_URL}/orgs/${selectedOrgHandle}/users`,
                    headers: {
                        Authorization: `Bearer ${token}`,
                        Cookie: `cwatf=${cwatf}`
                    }
                }).then((response) => {
                    const returnedUser = response.body.find(user => user.idpId === idpId);
                    user.id = returnedUser.id;

                    const STORAGE_KEY = "PORTAL_STATE";
                    cy.wrap(user).as("loggedInUser");
                    localStorage.setItem(
                        STORAGE_KEY,
                        JSON.stringify({
                            userInfo: {
                                isAuthenticated: true,
                                isAuthInProgress: false,
                                selectedOrgHandle: selectedOrgHandle,
                                isOrgAdmin: true,
                                user: user,
                            },
                        })
                    );

                    cy.setCookie('cwatf', cwatf);
                    cy.setCookie('cbearer', cbearer);
                    cy.setCookie('token', token);
                    cy.setCookie('id_token', data["id_token"]);
                    cy.log('Local storage set successful!, navigating to URL: '+ testURL)
                    cy.intercept(/choreo.dev/, (req) => {
                        if (req.url.includes("/linkersec/checklink")) {
                            req.headers['cookie'] = "cwatf=" + cwatf + "; " + req.headers['cookie'];
                            req.headers['authentication'] = "Bearer " + data["id_token"];
                        } else if (req.url.includes("/api/am/")) {
                            req.headers['cookie'] = "cwatf=" + cwatf + "; cbearer=" + cbearer;
                            req.headers['authentication'] = "Bearer " + apimTokenResponse.access_token;
                        } else {
                            req.headers['cookie'] = "cwatf=" + cwatf + "; cbearer=" + cbearer;
                            req.headers['authentication'] = "Bearer " + data["id_token"];
                        }
                    });
                    cy.hideWelcomeMessage();
                    cy.visit(Cypress.env('baseUrl'));
                    cy.log('Successfully logged in');
                });
            });
        })
    } catch (err) {
        throw new Error("Retrieving Access token failed : " + err);
    }
    return cy.get("@loggedInUser");
})
