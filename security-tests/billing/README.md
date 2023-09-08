1. The `PAYMENT_METHOD_ID_1` of the request 8 and 9 payloads should be replaced with the payment method ID you get after executing the 7th request which is a Stripe endpoint. Similarly, `PAYMENT_METHOD_ID_2` of request 11 should be replaced with the response payment method id of the request 12.
2. `INVOICE_ID` path parameter of the request 19 should be replaced with an “invoice id” you get with the request 18.
3. `SUBSCRIPTION_ID` path parameter of request 17 should be replaced with the subscription id of the response of request 9.
4. **“Register Azure subscription”** request cannot be tested as we need to create a subscription from Azure Marketplace and obtain the Azure token as a prerequisite for this.
5. **“Get component's step usage”** request not working, and we couldn’t find out the usage of it.
6. **“Update subscription (Deprecated)”** method cannot be tested as we have blocked the creation of step based subscriptions.