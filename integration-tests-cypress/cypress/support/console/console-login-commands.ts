import qs from 'qs';

Cypress.Commands.add('consoleUserLogin', () => {
    cy.visit('/');
    const testURL = Cypress.env('testURL');
    const idpURL = Cypress.env('idpURL');
    const idpUsername = Cypress.env('idpUsername');
    const idpPassword = Cypress.env('idpPassword');
    const idpAuthHeader = Cypress.env('idpAuthHeader');
    const selectedOrgHandle = Cypress.env('selectedOrgHandle');
    const name =  Cypress.env('name');
    const email = Cypress.env('email');
    const picURL = Cypress.env('picURL');
    const orgs = Cypress.env('orgs');
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
        }
        ).then((response) => {
            const data = response["body"];
            cy.log('Data received from the IDP');
            const fragments = data["id_token"].split(".");
            const token = fragments[0] + "." + fragments[1];
            const cwatf = fragments[2];
            const cbearer = fragments[0] + "." + fragments[1];
            cy.log('Starting login process...');

            Cypress.Cookies.defaults({
                preserve: ['cwatf', 'cbearer', 'id_token', 'token']
            });
            
            const STORAGE_KEY = "PORTAL_STATE";
            localStorage.setItem(
                STORAGE_KEY,
                JSON.stringify({
                    userInfo: {
                        isAuthenticated: true,
                        isAuthInProgress: false,
                        selectedOrgHandle: selectedOrgHandle,
                        user: {
                            name: name,
                            email: email,
                            token: token,
                            picURL: picURL,
                            orgs: orgs,
                        },
                    },
                })
            );

            cy.setCookie('cwatf', cwatf);
            cy.setCookie('cbearer', cbearer);
            cy.setCookie('token', token);
            cy.setCookie('id_token', data["id_token"]);
            cy.log('Local storage set successful!, navigating to URL: ', testURL)
            cy.intercept(/choreo.dev/, (req) => {
                
                if (req.url.includes("/linkersec/checklink")) {
                    req.headers['cookie'] = "cwatf=" + cwatf + "; " + req.headers['cookie'];
                    req.headers['authentication'] = "Bearer " + data["id_token"];
                } else {
                    req.headers['cookie'] = "cwatf=" + cwatf + "; cbearer=" + cbearer;
                    req.headers['authentication'] = "Bearer " + data["id_token"];
                }
            })

            cy.visit('/');
        })
    } catch (err) {
        throw new Error("Retrieving Access token failed : " + err);
    }
})
