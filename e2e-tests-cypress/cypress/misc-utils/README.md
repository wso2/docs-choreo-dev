# Miscellaneous Cypress Utilities

This folder contains miscellaneous utils implemented in Cypress that are not tests in themselves but are useful for performing routine house keeping tasks in Choreo that is required for test stability.

## Currently supported functionality

`test-data-cleanup.ts` - Can be used to cleanup any remaining data that has been introduced by Cypress E2E tests in Choreo.

## How to execute

```
npx cypress run --config-file cypress-misc-utils.json  --spec "cypress/misc-utils/test-data-cleanup.ts"
```