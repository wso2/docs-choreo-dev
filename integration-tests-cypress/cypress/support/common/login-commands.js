// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
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
            cy.visit('/');
        })
    }),

    Cypress.Commands.add('userLogout', () => {
        cy.log("Logout from Choreo");
        cy.get('[id="current-user"]').click();
        cy.contains('Logout').click();
        cy.url().should('include', '/login');
        cy.clearCookies();
        cy.clearLocalStorage();
        cy.window().then((win) => {
            win.sessionStorage.clear()
        });
        cy.log("logout successfully");
    })
