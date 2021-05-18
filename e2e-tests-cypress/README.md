# integration-tests-cypress

## Setup
1. Proceed to `e2e-tests-cypress` and run 
`npm install`

2. Configure the following properties in `cypress.env.json`

```text
username
password
```

3. Run the following command to run the tests:
	In interactive mode: `npx cypress open`
	In headless mode: `npm run test`