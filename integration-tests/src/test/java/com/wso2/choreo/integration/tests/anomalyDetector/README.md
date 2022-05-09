<h1>Tests</h1>

<h2>1. backendFailureAnomaly </h2>
<h3>Overview</h3>
This scenario tests if the multivariete Anomaly Detector detects a backend failure in a Choreo component
<br><br>
<h3> Implementation details </h3>
1. An Asgardeo and Choreo account dedicated for this test were created in each environment. An asgardeo account was first created to enable password based authentication. <br>
2. A passthrough component has been created in the above Choreo account. This passthrough component calls an HTTP backend. A non-existent URL was intentionally configured as the backend's endpoint in this passthrough component. This is so that when the passthrough service is invoked, it calls the backend and gets HTTP 404 errors. <br>
3. During the test, the above passthrough component is invoked. Since it's backend returns errors, the error count of the passthrough component increases. <br>
4. Anomaly detector detects this increase in errors and sends an email alert to the email address associated with the Choreo account. <br>
5. Finally, the test checks if the anomaly alert email was received. This is done by logging into the email account and filtering the email using the alert email's subject and timestamp.
<br><br>
<h3>NOTES</h3>
1. Anomaly Detector has an email suppression logic which works as follows. For a given component, once an anomaly is detected, an alert is sent. Thereafter alerts will be suppressed for a period of 15 minutes even if anomalies are detected during this period (This is to prevent flooding the user with alerts). If new anomalies are detected during this suppressed period, the suppression window moves forward and alerts will be suppressed for 15 minutes from the time of detection of the last anomaly. Therefore, if you re-run this test at close intervals, it can fail. <br>
2. Citrus logs will show HTTP 404 errors for the passthrough invocations during the test. This is normal due to the nature of this test.