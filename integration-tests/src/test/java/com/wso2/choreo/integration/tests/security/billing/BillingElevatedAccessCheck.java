package com.wso2.choreo.integration.tests.security.billing;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.SecurityTestContext;
import com.wso2.choreo.integration.common.utils.SecurityUtils;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.config.SecurityConfigDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class BillingElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgUuid;
    private static String paymentMethodId;
    private static String subscriptionId;
    private static String invoiceId;
    HttpClient citrusClient;

    @BeforeClass
    public void setup_BillingElevatedAccessCheck() throws Exception {
        accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        citrusClient = CitrusEndpoints.http().client().requestUrl(com.wso2.choreo.integration.config.Configuration.
                getSecurityConfig(SecurityConfigDefinition.BILLING_HOST_NAME)).build();
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        paymentMethodId = Configuration.getSecurityConfig(SecurityConfigDefinition.BILLING_PAYMENT_METHOD_ID);
        subscriptionId = Configuration.getSecurityConfig(SecurityConfigDefinition.BILLING_SUBSCRIPTION_ID);
        invoiceId = Configuration.getSecurityConfig(SecurityConfigDefinition.BILLING_INVOICE_ID);
    }

    @Test
    @CitrusTest
    public void getSubscriptions_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/subscriptions?cloudType=choreo";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, citrusClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void submitEnterpriseUpgradeRequest_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/notifications/enterprise";
        Map<String, String> params = new HashMap<>();
        params.put("ORG_UUID", orgUuid);
        String body = MessageUtils.generateStringFromTemplate("templates/billing/" +
                "submitEnterpriseUpgradeRequest.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, citrusClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void sendEmailsForSubscriptionUpgrade_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/notifications";
        Map<String, String> params = new HashMap<>();
        params.put("ORG_UUID", orgUuid);
        String body = MessageUtils.generateStringFromTemplate("templates/billing/" +
                "sendEmailsForSubscriptionUpgrade.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, citrusClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createBillingOrganization_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations";
        Map<String, String> params = new HashMap<>();
        params.put("ORG_UUID", orgUuid);
        String body = MessageUtils.generateStringFromTemplate("templates/billing/" +
                "createBillingOrg.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, citrusClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void attachPaymentMethod_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/payment-methods";
        Map<String, String> params = new HashMap<>();
        params.put("PAYMENT_METHOD_ID", paymentMethodId);
        String body = MessageUtils.generateStringFromTemplate("templates/billing/" +
                "attachPaymentMethod.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, citrusClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createSubscription_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/subscriptions";
        Map<String, String> params = new HashMap<>();
        params.put("PAYMENT_METHOD_ID", paymentMethodId);
        String body = MessageUtils.generateStringFromTemplate("templates/billing/" +
                "createSubscription.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, citrusClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getPaymentMethods_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/payment-methods";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, citrusClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void detachPaymentMethod_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/payment-methods/" + paymentMethodId;
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, citrusClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getBillingOrganization_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, citrusClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void getOverview_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/billing-overview?cloudType=choreo";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, citrusClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void updateBillingOrganization_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid;
        String body = MessageUtils.generateStringFromTemplate("templates/billing/" +
                "updateBillingOrganization.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, citrusClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateSubscription_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/subscriptions/" + subscriptionId + "/v2";
        Map<String, String> params = new HashMap<>();
        params.put("PAYMENT_METHOD_ID", paymentMethodId);
        String body = MessageUtils.generateStringFromTemplate("templates/billing/" +
                "updateSubscription.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, citrusClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void retrieveInvoices_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/invoices?cloudType=choreo";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, citrusClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void retrieveInvoiceByID_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/invoices/" + invoiceId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, citrusClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void cleanupBillingOrganization_BillingElevatedAccessCheck() throws Exception {
        String requestUrl = "/api/organizations/" + orgUuid + "/clean-up";
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, citrusClient, requestUrl, accessToken);
    }
}
