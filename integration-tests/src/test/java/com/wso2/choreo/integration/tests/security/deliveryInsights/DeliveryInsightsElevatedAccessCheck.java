package com.wso2.choreo.integration.tests.security.deliveryInsights;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.TestContext;
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

public class DeliveryInsightsElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgId;
    private static String dataPlaneId;
    private static String startTime;
    private static String endTime;
    private static String projectId;
    private static String orgName;
    private static String repoName;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_DeliveryInsightsElevatedAccessCheck() throws Exception {
        accessToken = TestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        //accessToken = "Bearer eyJ4NXQiOiJOMkprTmpZMllUZGtabVl4TldNNVltSTJabUkwWlRFNE56ZzRNREkxTVRneVpUaGpaVEppWWciLCJraWQiOiJNbUV5WlRSaFpHTTROamc1WW1SbU9XVXlOalkxT1dReVpURXlNREJoTXpVd01ESTFOak5pWlRkalptWXhZMlkzWWpCaU4ySTRaRFppTW1Jek5qYzJPUV9SUzI1NiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjOGQwMzg2Ni02OTQyLTRiNmQtYTE5OS00NGFkZmM1YTk2M2UiLCJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiaXNzIjoiaHR0cHM6XC9cL3N0cy5wcmV2aWV3LWR2LmNob3Jlby5kZXY6NDQzXC9vYXV0aDJcL3Rva2VuIiwiYXVkIjpbIld4cXkwbGlDZkxCc2RwWE9oa2N4Wno2dUxQa2EiLCJodHRwczpcL1wvc3RzLnByZXZpZXctZHYuY2hvcmVvLmRldjo0NDNcL29hdXRoMlwvdG9rZW4iXSwibmJmIjoxNjkyMDc3NTI5LCJhenAiOiJXeHF5MGxpQ2ZMQnNkcFhPaGtjeFp6NnVMUGthIiwic2NvcGUiOiJhcGltOmFkbWluIGFwaW06YXBpX2dlbmVyYXRlX2tleSBhcGltOmFwaV9tYW5hZ2UgYXBpbTphcGlfcHVibGlzaCBhcGltOmFwaV9zZXR0aW5ncyBhcGltOmFwaV92aWV3IGFwaW06ZGNyOmFwcF9tYW5hZ2UgYXBpbTpkb2N1bWVudF9tYW5hZ2UgYXBpbTplbnZpcm9ubWVudF9tYW5hZ2UgYXBpbTpwdWJsaXNoZXJfc2V0dGluZ3MgYXBpbTpzdWJzY3JpYmUgYXBpbTpzdWJzY3JpcHRpb25fbWFuYWdlIGFwaW06c3Vic2NyaXB0aW9uX3ZpZXcgYXBpbTp0aWVyX21hbmFnZSBhcGltOnRpZXJfdmlldyBjaG9yZW86Y29tcG9uZW50X21hbmFnZSBjaG9yZW86ZGVwbG95bWVudF9tYW5hZ2UgY2hvcmVvOmRldl9lbnZfbWFuYWdlIGNob3Jlbzpsb2dfdmlld19ub25fcHJvZCBjaG9yZW86bG9nX3ZpZXdfcHJvZCBjaG9yZW86bm9uX3Byb2RfZW52X21hbmFnZSBjaG9yZW86cHJvZF9lbnZfbWFuYWdlIGNob3Jlbzpwcm9qZWN0X21hbmFnZSBlbnZpcm9ubWVudHM6dmlld19kZXYgZW52aXJvbm1lbnRzOnZpZXdfcHJvZCB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNob3Jlb2F1ZGl0bG9nZ2luZ2FwaTphdWRpdF9sb2dzX21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNob3Jlb2F1ZGl0bG9nZ2luZ2FwaTphdWRpdF9sb2dzX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjaG9yZW9kZXZvcHNwb3J0YWxhcGk6Y29tcG9uZW50X21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNob3Jlb2Rldm9wc3BvcnRhbGFwaTpkZXBsb3ltZW50X21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNob3Jlb2Rldm9wc3BvcnRhbGFwaTpkZXBsb3ltZW50X3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb21wb25lbnRzbWFuYWdlbWVudDpjb21wb25lbnRfY29uZmlnX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb21wb25lbnRzbWFuYWdlbWVudDpjb21wb25lbnRfY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29tcG9uZW50c21hbmFnZW1lbnQ6Y29tcG9uZW50X2ZpbGVfdmlldyB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbXBvbmVudHNtYW5hZ2VtZW50OmNvbXBvbmVudF9pbml0X3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb21wb25lbnRzbWFuYWdlbWVudDpjb21wb25lbnRfbG9nc192aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29tcG9uZW50c21hbmFnZW1lbnQ6Y29tcG9uZW50X21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbXBvbmVudHNtYW5hZ2VtZW50OmNvbXBvbmVudF90cmlnZ2VyIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29tcG9uZW50dXRpbHM6Y29tcG9uZW50X2ZpbGVfdmlldyB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbXBvbmVudHV0aWxzOmNvbXBvbmVudF9sb2dzX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb21wb25lbnR1dGlsczpjb21wb25lbnRfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29tcG9uZW50dXRpbHM6Y29tcG9uZW50X3RyaWdnZXIgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50OmNvbmZpZ19jcmVhdGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50OmNvbmZpZ19kZWxldGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50OmNvbmZpZ19tYW5hZ2UgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50OmNvbmZpZ192aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29uZmlnbWFuYWdlbWVudDpnbG9iYWxfY29uZmlnX2NyZWF0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbmZpZ21hbmFnZW1lbnQ6Z2xvYmFsX2NvbmZpZ19kZWxldGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50Omdsb2JhbF9jb25maWdfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29uZmlnbWFuYWdlbWVudDpnbG9iYWxfY29uZmlnX3VwZGF0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbmZpZ21hbmFnZW1lbnQ6Z2xvYmFsX2NvbmZpZ192aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fZGVsZXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fdXBkYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fdmlldyB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9ucHJlbWtleW1hbmFnZW1lbnQ6b25fcHJlbV9rZXlfY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6b25wcmVta2V5bWFuYWdlbWVudDpvbl9wcmVtX2tleV9kZWxldGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvbnByZW1rZXltYW5hZ2VtZW50Om9uX3ByZW1fa2V5X21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9ucHJlbWtleW1hbmFnZW1lbnQ6b25fcHJlbV9rZXlfdXBkYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6b25wcmVta2V5bWFuYWdlbWVudDpvbl9wcmVtX2tleV92aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6b3JnYW5pemF0aW9ubWFuYWdlbWVudDplbnRlcnByaXNlX2xvZ2luX2NvbmZpZ19tYW5hZ2UgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvcmdhbml6YXRpb25tYW5hZ2VtZW50OmVudGVycHJpc2VfbG9naW5fY29uZmlnX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvcmdhbml6YXRpb25tYW5hZ2VtZW50OnNlbGZfc2lnbnVwX2FwcHJvdmFsX3VwZGF0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6c2VsZl9zaWdudXBfYXBwcm92YWxfdmlldyB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6c2VsZl9zaWdudXBfY29uZmlnX3VwZGF0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6c2VsZl9zaWdudXBfY29uZmlnX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvcmdhbml6YXRpb25tYW5hZ2VtZW50OnNlbGZfc2lnbnVwX21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6dGhlbWVfY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6b3JnYW5pemF0aW9ubWFuYWdlbWVudDp0aGVtZV9kZWxldGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvcmdhbml6YXRpb25tYW5hZ2VtZW50OnRoZW1lX2RlcGxveSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6dGhlbWVfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6b3JnYW5pemF0aW9ubWFuYWdlbWVudDp0aGVtZV92aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50Omludml0YXRpb25fZGVsZXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50Omludml0YXRpb25fbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50Omludml0YXRpb25fc2VuZCB1cm46Y2hvcmVvY29udHJvbHBsYW5lOnVzZXJzbWFuYWdlbWVudDppbnZpdGF0aW9uX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cGVybWlzc2lvbl92aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfZGVsZXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfbWFwcGluZ19jcmVhdGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cm9sZV9tYXBwaW5nX2RlbGV0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOnVzZXJzbWFuYWdlbWVudDpyb2xlX21hcHBpbmdfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfbWFwcGluZ191cGRhdGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cm9sZV9tYXBwaW5nX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cm9sZV91cGRhdGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cm9sZV92aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnVzZXJfZGVsZXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnVzZXJfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnVzZXJfdXBkYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnVzZXJfdmlldyIsIm9yZ2FuaXphdGlvbiI6eyJoYW5kbGUiOiJjaG9yZW9yYmFjdGVzdHVzZXIiLCJ1dWlkIjoiNzVkNzVjMGQtMzM0Zi00OTM4LWEzNmYtZWRmYmMxMjA0MWVkIn0sIm9yZ2FuaXphdGlvbnMiOlsiNzVkNzVjMGQtMzM0Zi00OTM4LWEzNmYtZWRmYmMxMjA0MWVkIiwiMzFiOWUzZmUtNGE5Mi00OTIxLThiZTEtNzBmMDY1YjViNDA2IiwiYWJmNjRjM2ItMjU4ZC00NzQ2LTgyNzktZDZjNWY2M2VhNTU4Il0sImV4cCI6MTY5MjA4MTEyOSwiaWRwX2NsYWltcyI6eyJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiZW1haWwiOiJjaG9yZW9yYmFjdGVzdHVzZXJAZ21haWwuY29tIn0sImlhdCI6MTY5MjA3NzUyOSwianRpIjoiNWZjMGYyNGYtNTc4Yy00Y2E0LTlmZmQtMTM3Zjk4NDJlOTI3In0.o9DmGPC7muI5Gfi5E9NDrh8J6CvDLCr2nosUSgbq3m2KwPkbOa9BLGR2YIu6OUA9L1LL_8hFoo_Z40Iu9SS6eq-V-qKIPY9eHK_-EEky86ogy-JY4Fx-HT-lRytMWVwyzXLk5Aav3BGkjrEShcfq5C6SLwIyotdbac449K-5wUDoFLCPexI-pSKxcSilFsBPsz5j_aoOkQpMkn9mXEtuPvijYU4A2Lb9FTeQFHSFGmdh-8ksl1S63MOPNtFjbp-urq-KADrQAemA16Bl31BQGlKZJHJ3bk-qMzZ22aNyhdcNg4g_0hjCBZ_Y3UHi4GMI-rXPPitsADiXj_8iQHkpyi80AduIB5OnFoxTdKI_ibwQ0quwcxdVHT_AR9l5VgjKFGm2tRxn63z9FXuaudcGymZ1ZIETP-iDBpyOzdAIKYzdYHZotVsIwRL6jcWlp-b3EP8UrD_IN1qK5uHSn-3YKOwtwoULdeYErkqG3YWUW9YowJaqcnRhM8kLbPPL0sXnfOpVo4wcZS1rh8Cx3vFf7taXZK0TVY1tq8oPUmhAwzjW0Tu7btAKQIu2-FMOW6DzCGE5Ksve2YB626fm-Tq1erQv3ovx96Vq64fSOHG1Sd8SaAlSlyXuRsLQu_Ex4oVrGBIxkgjInjeKuWAXVWwS7oWjKz97UfuS5i_gKuAmHJA";
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        // orgId = "75d75c0d-334f-4938-a36f-edfbc12041ed";
        dataPlaneId = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_DATAPLANE_ID);
        startTime = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_START_TIME);
        endTime = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_END_TIME);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_PROJECT_ID);
        orgName = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_ORG_NAME);
        repoName = Configuration.getSecurityConfig(SecurityConfigDefinition.DI_REPO_NAME);
    }

    @Test
    @CitrusTest
    public void addIncidentScrapperConfigurations_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForAddIncidentScrapperConfigurations = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/github";
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("dataPlaneId", dataPlaneId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForAddIncidentScrapperConfigurations.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForAddIncidentScrapperConfigurations, body, accessToken);

    }

    @Test
    @CitrusTest
    public void postOrganizationMemberCount_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForAddIncidentScrapperConfigurations = Constant.CIO_QUERY_API;
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostOrganizationMemberCount.mustache", null);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForAddIncidentScrapperConfigurations, body, accessToken);

    }

    @Test
    @CitrusTest
    public void postDeploymentsTimeSeriesData_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostDeploymentsTimeSeriesData = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostDeploymentsTimeSeriesData.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostDeploymentsTimeSeriesData, body, accessToken);

    }

    @Test
    @CitrusTest
    public void postDeploymentFrequencySummary_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostDeploymentFrequencySummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostDeploymentFrequencySummary.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostDeploymentFrequencySummary, body, accessToken);

    }

    @Test
    @CitrusTest
    public void postLeadTimeSummaryData_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostDeploymentFrequencySummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostLeadTimeSummaryData.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostDeploymentFrequencySummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateIncidentConfigRepository_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostDeploymentFrequencySummary = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/github/" + orgId + "/repository";
        Map<String, String> params = new HashMap<>();
        params.put("org_name", orgName);
        params.put("repo_name", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForUpdateIncidentConfigRepository.mustache", params);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForPostDeploymentFrequencySummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateIncidentConfigSelectorCriteria_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateIncidentConfigSelectorCriteria = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/" + orgId + "/selectorCriteria";
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForUpdateIncidentConfigSelectorCriteria.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForUpdateIncidentConfigSelectorCriteria, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateIncidentConfigRejectorCriteria_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForUpdateIncidentConfigRejectorCriteria = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/" + orgId + "/rejectorCriteria";
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForUpdateIncidentConfigRejectorCriteria.mustache", null);
        SecurityUtils.elevatedAccessCheckForPutRequests(this, choreoCPTestClient,
                requestUrlForUpdateIncidentConfigRejectorCriteria, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getScraperConfigurations_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetScraperConfigurations = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/github/console?orgId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetScraperConfigurations, accessToken);
    }

    @Test
    @CitrusTest
    public void postFailureRateSummary_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostFailureRateSummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostFailureRateSummary.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostFailureRateSummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void postFailureRateDetails_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostFailureRateSummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostFailureRateDetails.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostFailureRateSummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void postRecoveryTimeDetails_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostRecoveryTimeDetails = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostRecoveryTimeDetails.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostRecoveryTimeDetails, body, accessToken);
    }

    @Test
    @CitrusTest
    public void postRecoveryTimeSummary_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostRecoveryTimeSummary = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostRecoveryTimeSummary.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostRecoveryTimeSummary, body, accessToken);
    }

    @Test
    @CitrusTest
    public void postTopPerformingProjects_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostTopPerformingProjects = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostTopPerformingProjects.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostTopPerformingProjects, body, accessToken);
    }

    @Test
    @CitrusTest
    public void checkRepoAccessibility_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostTopPerformingProjects = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations/github/repo-accessibility?orgId=" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForPostTopPerformingProjects, accessToken);
    }

    @Test
    @CitrusTest
    public void getDataplanes_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetDataplanes = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/dataplanes/" + orgId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetDataplanes, accessToken);
    }

    @Test
    @CitrusTest
    public void postActiveDeveloperCount_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForPostActiveDeveloperCount = Constant.CIO_QUERY_API;
        Map<String, String> params = new HashMap<>();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        String body = MessageUtils.generateStringFromTemplate("templates/deliveryInsights/" +
                "queryForPostActiveDeveloperCount.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForPostActiveDeveloperCount, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteScraperConfigs_DeliveryInsightsElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForDeleteScraperConfigs = Constant.CIO_INCIDENT_CONFIGURATOR +
                "/configurations?dashboardKind=innov-perf-github";
        SecurityUtils.elevatedAccessCheckForDeleteRequests(this, choreoCPTestClient,
                requestUrlForDeleteScraperConfigs, accessToken);
    }
}
