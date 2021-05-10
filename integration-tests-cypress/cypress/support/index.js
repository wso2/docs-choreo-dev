// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import '@testing-library/cypress/add-commands'
import './common/login-commands'
import './common/choreo-commands'
import './devportal/devportal-commands'
import './console/console-login-commands'

// Alternatively you can use CommonJS syntax:
// require('./commands')

// Overwrite log command to use task to put all cy.log() messages to console output
Cypress.Commands.overwrite('log', (subject, message) => cy.task('log', message));
