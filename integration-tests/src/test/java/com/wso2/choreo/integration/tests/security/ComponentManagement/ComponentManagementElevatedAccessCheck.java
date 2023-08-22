package com.wso2.choreo.integration.tests.security.ComponentManagement;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.common.Endpoints;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.common.SecurityTestContext;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
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

public class ComponentManagementElevatedAccessCheck extends TestNGCitrusSpringSupport {
    private static String accessToken;
    private static String orgId;
    private static String orgUuid;
    private static String orgHandler;
    private static String projectName;
    private static String projectId;
    private static String componentName;
    private static String ballerinaVersion;
    private static String componentId;
    private static String srcGitRepoUrl;
    private static String repositorySubPath;
    private static String requestUrl;
    private static String componentType;
    private static String oasFilePath;
    private static String dockerfilePath;
    private static String dockerContext;
    private static String componentHandler;
    private static String versionId;
    private static String envId;
    private static String branch;
    private static String sha;
    private static String shaDate;
    private static String releaseId;
    private static String repoName;
    private static String commitHash;
    private static String apiId;
    private static String gitOrgHandle;
    private static String testUserId;
    private static String secretRef;
    private static String bitbucketOrgName;
    private static String appPwd;
    private static String credentialID;
    private static String credentialName;
    private static String targetEnvironmentId;
    private static String runID;
    private static String endpointId;
    private static String apiDefinitionPath;
    private static String displayName;
    private static String apiContext;
    private static String visibility;
    private static String buildId;
    @Autowired
    Map<Endpoints, HttpClient> citrusClients;

    @BeforeClass
    public void setup_ComponentManagementElevatedAccessCheck() throws Exception {
        requestUrl = Constant.COMPONET_MGT_SUFFIX;
        //accessToken = SecurityTestContext.getTestUserTokenHandlerForSecurityTests().getTestTokenForCPAPIs();
        accessToken = "eyJ4NXQiOiJOMkprTmpZMllUZGtabVl4TldNNVltSTJabUkwWlRFNE56ZzRNREkxTVRneVpUaGpaVEppWWciLCJraWQiOiJNbUV5WlRSaFpHTTROamc1WW1SbU9XVXlOalkxT1dReVpURXlNREJoTXpVd01ESTFOak5pWlRkalptWXhZMlkzWWpCaU4ySTRaRFppTW1Jek5qYzJPUV9SUzI1NiIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJjOGQwMzg2Ni02OTQyLTRiNmQtYTE5OS00NGFkZmM1YTk2M2UiLCJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiaXNzIjoiaHR0cHM6XC9cL3N0cy5wcmV2aWV3LWR2LmNob3Jlby5kZXY6NDQzXC9vYXV0aDJcL3Rva2VuIiwiYXVkIjpbIld4cXkwbGlDZkxCc2RwWE9oa2N4Wno2dUxQa2EiLCJodHRwczpcL1wvc3RzLnByZXZpZXctZHYuY2hvcmVvLmRldjo0NDNcL29hdXRoMlwvdG9rZW4iXSwibmJmIjoxNjkyMDc3NTI5LCJhenAiOiJXeHF5MGxpQ2ZMQnNkcFhPaGtjeFp6NnVMUGthIiwic2NvcGUiOiJhcGltOmFkbWluIGFwaW06YXBpX2dlbmVyYXRlX2tleSBhcGltOmFwaV9tYW5hZ2UgYXBpbTphcGlfcHVibGlzaCBhcGltOmFwaV9zZXR0aW5ncyBhcGltOmFwaV92aWV3IGFwaW06ZGNyOmFwcF9tYW5hZ2UgYXBpbTpkb2N1bWVudF9tYW5hZ2UgYXBpbTplbnZpcm9ubWVudF9tYW5hZ2UgYXBpbTpwdWJsaXNoZXJfc2V0dGluZ3MgYXBpbTpzdWJzY3JpYmUgYXBpbTpzdWJzY3JpcHRpb25fbWFuYWdlIGFwaW06c3Vic2NyaXB0aW9uX3ZpZXcgYXBpbTp0aWVyX21hbmFnZSBhcGltOnRpZXJfdmlldyBjaG9yZW86Y29tcG9uZW50X21hbmFnZSBjaG9yZW86ZGVwbG95bWVudF9tYW5hZ2UgY2hvcmVvOmRldl9lbnZfbWFuYWdlIGNob3Jlbzpsb2dfdmlld19ub25fcHJvZCBjaG9yZW86bG9nX3ZpZXdfcHJvZCBjaG9yZW86bm9uX3Byb2RfZW52X21hbmFnZSBjaG9yZW86cHJvZF9lbnZfbWFuYWdlIGNob3Jlbzpwcm9qZWN0X21hbmFnZSBlbnZpcm9ubWVudHM6dmlld19kZXYgZW52aXJvbm1lbnRzOnZpZXdfcHJvZCB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNob3Jlb2F1ZGl0bG9nZ2luZ2FwaTphdWRpdF9sb2dzX21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNob3Jlb2F1ZGl0bG9nZ2luZ2FwaTphdWRpdF9sb2dzX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjaG9yZW9kZXZvcHNwb3J0YWxhcGk6Y29tcG9uZW50X21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNob3Jlb2Rldm9wc3BvcnRhbGFwaTpkZXBsb3ltZW50X21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNob3Jlb2Rldm9wc3BvcnRhbGFwaTpkZXBsb3ltZW50X3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb21wb25lbnRzbWFuYWdlbWVudDpjb21wb25lbnRfY29uZmlnX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb21wb25lbnRzbWFuYWdlbWVudDpjb21wb25lbnRfY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29tcG9uZW50c21hbmFnZW1lbnQ6Y29tcG9uZW50X2ZpbGVfdmlldyB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbXBvbmVudHNtYW5hZ2VtZW50OmNvbXBvbmVudF9pbml0X3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb21wb25lbnRzbWFuYWdlbWVudDpjb21wb25lbnRfbG9nc192aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29tcG9uZW50c21hbmFnZW1lbnQ6Y29tcG9uZW50X21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbXBvbmVudHNtYW5hZ2VtZW50OmNvbXBvbmVudF90cmlnZ2VyIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29tcG9uZW50dXRpbHM6Y29tcG9uZW50X2ZpbGVfdmlldyB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbXBvbmVudHV0aWxzOmNvbXBvbmVudF9sb2dzX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb21wb25lbnR1dGlsczpjb21wb25lbnRfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29tcG9uZW50dXRpbHM6Y29tcG9uZW50X3RyaWdnZXIgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50OmNvbmZpZ19jcmVhdGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50OmNvbmZpZ19kZWxldGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50OmNvbmZpZ19tYW5hZ2UgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50OmNvbmZpZ192aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29uZmlnbWFuYWdlbWVudDpnbG9iYWxfY29uZmlnX2NyZWF0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbmZpZ21hbmFnZW1lbnQ6Z2xvYmFsX2NvbmZpZ19kZWxldGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpjb25maWdtYW5hZ2VtZW50Omdsb2JhbF9jb25maWdfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y29uZmlnbWFuYWdlbWVudDpnbG9iYWxfY29uZmlnX3VwZGF0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOmNvbmZpZ21hbmFnZW1lbnQ6Z2xvYmFsX2NvbmZpZ192aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fZGVsZXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fdXBkYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6Y3VzdG9tZG9tYWluYXBpOmN1c3RvbV9kb21haW5fdmlldyB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9ucHJlbWtleW1hbmFnZW1lbnQ6b25fcHJlbV9rZXlfY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6b25wcmVta2V5bWFuYWdlbWVudDpvbl9wcmVtX2tleV9kZWxldGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvbnByZW1rZXltYW5hZ2VtZW50Om9uX3ByZW1fa2V5X21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9ucHJlbWtleW1hbmFnZW1lbnQ6b25fcHJlbV9rZXlfdXBkYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6b25wcmVta2V5bWFuYWdlbWVudDpvbl9wcmVtX2tleV92aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6b3JnYW5pemF0aW9ubWFuYWdlbWVudDplbnRlcnByaXNlX2xvZ2luX2NvbmZpZ19tYW5hZ2UgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvcmdhbml6YXRpb25tYW5hZ2VtZW50OmVudGVycHJpc2VfbG9naW5fY29uZmlnX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvcmdhbml6YXRpb25tYW5hZ2VtZW50OnNlbGZfc2lnbnVwX2FwcHJvdmFsX3VwZGF0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6c2VsZl9zaWdudXBfYXBwcm92YWxfdmlldyB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6c2VsZl9zaWdudXBfY29uZmlnX3VwZGF0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6c2VsZl9zaWdudXBfY29uZmlnX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvcmdhbml6YXRpb25tYW5hZ2VtZW50OnNlbGZfc2lnbnVwX21hbmFnZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6dGhlbWVfY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6b3JnYW5pemF0aW9ubWFuYWdlbWVudDp0aGVtZV9kZWxldGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTpvcmdhbml6YXRpb25tYW5hZ2VtZW50OnRoZW1lX2RlcGxveSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOm9yZ2FuaXphdGlvbm1hbmFnZW1lbnQ6dGhlbWVfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6b3JnYW5pemF0aW9ubWFuYWdlbWVudDp0aGVtZV92aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50Omludml0YXRpb25fZGVsZXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50Omludml0YXRpb25fbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50Omludml0YXRpb25fc2VuZCB1cm46Y2hvcmVvY29udHJvbHBsYW5lOnVzZXJzbWFuYWdlbWVudDppbnZpdGF0aW9uX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cGVybWlzc2lvbl92aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfY3JlYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfZGVsZXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfbWFwcGluZ19jcmVhdGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cm9sZV9tYXBwaW5nX2RlbGV0ZSB1cm46Y2hvcmVvY29udHJvbHBsYW5lOnVzZXJzbWFuYWdlbWVudDpyb2xlX21hcHBpbmdfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnJvbGVfbWFwcGluZ191cGRhdGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cm9sZV9tYXBwaW5nX3ZpZXcgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cm9sZV91cGRhdGUgdXJuOmNob3Jlb2NvbnRyb2xwbGFuZTp1c2Vyc21hbmFnZW1lbnQ6cm9sZV92aWV3IHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnVzZXJfZGVsZXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnVzZXJfbWFuYWdlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnVzZXJfdXBkYXRlIHVybjpjaG9yZW9jb250cm9scGxhbmU6dXNlcnNtYW5hZ2VtZW50OnVzZXJfdmlldyIsIm9yZ2FuaXphdGlvbiI6eyJoYW5kbGUiOiJjaG9yZW9yYmFjdGVzdHVzZXIiLCJ1dWlkIjoiNzVkNzVjMGQtMzM0Zi00OTM4LWEzNmYtZWRmYmMxMjA0MWVkIn0sIm9yZ2FuaXphdGlvbnMiOlsiNzVkNzVjMGQtMzM0Zi00OTM4LWEzNmYtZWRmYmMxMjA0MWVkIiwiMzFiOWUzZmUtNGE5Mi00OTIxLThiZTEtNzBmMDY1YjViNDA2IiwiYWJmNjRjM2ItMjU4ZC00NzQ2LTgyNzktZDZjNWY2M2VhNTU4Il0sImV4cCI6MTY5MjA4MTEyOSwiaWRwX2NsYWltcyI6eyJhdXQiOiJBUFBMSUNBVElPTl9VU0VSIiwiZW1haWwiOiJjaG9yZW9yYmFjdGVzdHVzZXJAZ21haWwuY29tIn0sImlhdCI6MTY5MjA3NzUyOSwianRpIjoiNWZjMGYyNGYtNTc4Yy00Y2E0LTlmZmQtMTM3Zjk4NDJlOTI3In0.o9DmGPC7muI5Gfi5E9NDrh8J6CvDLCr2nosUSgbq3m2KwPkbOa9BLGR2YIu6OUA9L1LL_8hFoo_Z40Iu9SS6eq-V-qKIPY9eHK_-EEky86ogy-JY4Fx-HT-lRytMWVwyzXLk5Aav3BGkjrEShcfq5C6SLwIyotdbac449K-5wUDoFLCPexI-pSKxcSilFsBPsz5j_aoOkQpMkn9mXEtuPvijYU4A2Lb9FTeQFHSFGmdh-8ksl1S63MOPNtFjbp-urq-KADrQAemA16Bl31BQGlKZJHJ3bk-qMzZ22aNyhdcNg4g_0hjCBZ_Y3UHi4GMI-rXPPitsADiXj_8iQHkpyi80AduIB5OnFoxTdKI_ibwQ0quwcxdVHT_AR9l5VgjKFGm2tRxn63z9FXuaudcGymZ1ZIETP-iDBpyOzdAIKYzdYHZotVsIwRL6jcWlp-b3EP8UrD_IN1qK5uHSn-3YKOwtwoULdeYErkqG3YWUW9YowJaqcnRhM8kLbPPL0sXnfOpVo4wcZS1rh8Cx3vFf7taXZK0TVY1tq8oPUmhAwzjW0Tu7btAKQIu2-FMOW6DzCGE5Ksve2YB626fm-Tq1erQv3ovx96Vq64fSOHG1Sd8SaAlSlyXuRsLQu_Ex4oVrGBIxkgjInjeKuWAXVWwS7oWjKz97UfuS5i_gKuAmHJA";
        orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        orgId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
        orgHandler = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        projectName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_PROJECT_NAME);
        projectId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_PROJECT_ID);
        componentName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMPONENT_NAME);
        ballerinaVersion = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_BALLERINA_VERSION);
        componentId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMPONENT_ID);
        srcGitRepoUrl = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_SRC_GIT_REPO_URL);
        repositorySubPath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_REPOSITORY_SUB_PATH);
        componentType = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMPONENT_TYPE);
        oasFilePath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_OAS_FILE_PATH);
        dockerfilePath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_DOCKER_FILE_PATH);
        dockerContext = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_DOCKER_CONTEXT);
        componentHandler = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMPONENT_HANDLER);
        versionId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_VERSION_ID);
        envId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_ENV_ID);
        branch = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_BRANCH);
        sha = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_SHA);
        shaDate = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_SHA_DATE);
        releaseId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_RELEASE_ID);
        repoName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_REPO_NAME);
        commitHash = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_COMMIT_HASH);
        apiId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_API_ID);
        gitOrgHandle = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_GIT_ORG_HANDLE);
        testUserId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_TEST_USER_ID);
        secretRef = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_SECRET_REF);
        bitbucketOrgName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_BITBUCKET_ORG_NAME);
        appPwd = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_APP_PWD);
        credentialID = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_CREDENTIAL_ID);
        credentialName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_CREDENTIAL_NAME);
        targetEnvironmentId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_TARGET_ENV_ID);
        runID = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_RUN_ID);
        endpointId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_ENDPOINT_ID);
        apiDefinitionPath = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_API_DEF_PATH);
        displayName = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_DISPLAY_NAME);
        apiContext = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_API_CONTEXT);
        visibility = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_VISIBILITY);
        buildId = Configuration.getSecurityConfig(SecurityConfigDefinition.CM_BUILD_ID);
    }

    @Test
    @CitrusTest
    public void createProject_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("projectName", projectName);
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "createProject.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getProjects_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getProjects.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getProject_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getProject.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void listProjects_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "listProjects.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentName", componentName);
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        params.put("ballerinaVersion", ballerinaVersion);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "createComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "updateComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateProject_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("orgId", orgId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "updateProject.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createIntegrationComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentName", componentName);
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        params.put("srcGitRepoUrl", srcGitRepoUrl);
        params.put("repositorySubPath", repositorySubPath);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "createIntegrationComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createByocComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("name", componentName);
        params.put("orgId", orgId);
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        params.put("componentType", componentType);
        params.put("oasFilePath", oasFilePath);
        params.put("dockerfilePath", dockerfilePath);
        params.put("dockerContext", dockerContext);
        params.put("srcGitRepoUrl", srcGitRepoUrl);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "createBYOCcomponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void componentDetails_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("projectId", projectId);
        params.put("componentHandler", componentHandler);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "componentDetails.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void listAllComponents_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "listAllComponents.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void envDetails_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getEnvironments.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deployComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("latestVersionId", versionId);
        params.put("devEnvIdToDeploy", envId);
        params.put("branch", branch);
        params.put("sha", sha);
        params.put("shaDate", shaDate);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "deployComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void manuallyDeployToDev_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("orgUuid", orgUuid);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("environmentId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "componentDeployment.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getBuildsByVersion_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        String body = MessageUtils.generateStringFromTemplate("templates/maxApiRevisions/" +
                "query_build_by_version_payload.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void commitHistory_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("branch", branch);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "commitHistoryBranch.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void handleDisableAutoBuild_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("envId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "handleDisableAutoBuild.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getCommonCredentials_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "commonCredentials.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getDeploymentStatusByVersion_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("latestVersionId", versionId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "deploymentStatusByVersion.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void stopDeployment_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("releaseId", releaseId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "stopDeployment.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getRepoMetadataGH_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("repoName", repoName);
        params.put("branch", branch);
        params.put("repositorySubPath", repositorySubPath);
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "repoMetadata.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void repoBranchListGH_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("repoName", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "repoBranchList.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getComponentWebhook_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "componentWebhook.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getConfigurationCommitMappings_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetConfigurationCommitMappings = "/component-mgt/1.0.0/orgs/" + orgHandler + "/projects/" + projectId + "/components/" +
                componentId + "/versions/" + versionId + "/commits/" + commitHash + "/configurable-commit-mapping";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetConfigurationCommitMappings, accessToken);
    }

    @Test
    @CitrusTest
    public void getBranchListInComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "branchListInComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createVersion_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("orgUuid", orgUuid);
        params.put("componentId", componentId);
        params.put("componentType", componentType);
        params.put("apiId", apiId);
        params.put("branch", branch);
        String body = MessageUtils.generateStringFromTemplate("templates/byor/" +
                "graphqlQueryForNewVersionCreation.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getConfigurations_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        requestUrl = "/config-mgt/1.0.0/orgs/" + orgHandler + "/projects/" + projectId + "/components/" + componentId +
                "/envs/" + envId + "/" + versionId + "/configurations?component_name=" + componentName +
                "&commit_hash=" + commitHash;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrl, accessToken);
    }

    @Test
    @CitrusTest
    public void configGeneration_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        requestUrl = "/component-mgt/1.0.0/orgs/" + orgHandler + "/projects/" + projectId +
                "/triggers/configurable-generation";
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("branch", branch);
        params.put("commitHash", commitHash);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "configGeneration.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getRepoContents_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetRepoContents = "/component-utils/1.0.0/repositories/" + gitOrgHandle + "/" + repoName + "/branches/main/contents?"
                + "userId=" + testUserId;
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetRepoContents,
                accessToken);
    }

    @Test
    @CitrusTest
    public void componenPullRequests_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getComponentPullRequests.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void autobuildTrigger_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "autobuildTrigger.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void gitTokenPermissions_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("bitbucketOrgName", bitbucketOrgName);
        params.put("appPwd", appPwd);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "gitTokenPermissionBB.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void createCommonCredential_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("bitbucketOrgName", bitbucketOrgName);
        params.put("appPwd", appPwd);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "commonCredential.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getUserReposGitHub_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("secretRef", secretRef);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "userReposGitHub.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void projectComponentLabels_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "projectComponentLabels.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateCommonCredential_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("credentialID", credentialID);
        params.put("credentialName", credentialName);
        params.put("orgUuid", orgUuid);
        params.put("bitbucketOrgName", bitbucketOrgName);
        params.put("appPwd", appPwd);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "updateCommonCredential.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getCellDiagram_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("commitHash", commitHash);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "cellDiagram.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void scanResult_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "scanResult.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void userRepoStatus_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "userRepoStatus.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void userRepoValidation_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("repoName", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "userRepoValidation.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void redeployDeployment_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("releaseId", releaseId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "redeployDeployment.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void buildsByVersion_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("devEnvId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/maxApiRevisions/" +
                "query_build_by_version_payload.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void distinctComponentTypeCount_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "distinctComponentTypeCount.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void invokeUrls_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "invokeUrls.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void invokeUrl_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("devEnvId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "invokeUrl.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void invokeInformation_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgUuid", orgUuid);
        params.put("orgHandler", orgHandler);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "invokeInformation.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void isValidNonEmptyRepo_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("gitOrgHandle", gitOrgHandle);
        params.put("repoName", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "isValidNonEmptyRepo.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void repoDirectoryList_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("gitOrgHandle", gitOrgHandle);
        params.put("repoName", repoName);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "repoDirectoryList.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void generateEndpoints_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("releaseId", releaseId);
        params.put("commitHash", commitHash);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "generateEndpoints.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void componentEndpoints_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("releaseId", releaseId);
        String body = MessageUtils.generateStringFromTemplate("templates/endpoints/" +
                "GetEndpoints.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void promoteEndpoints_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("releaseId", releaseId);
        params.put("targetEnvironmentId", targetEnvironmentId);
        String body = MessageUtils.generateStringFromTemplate("templates/endpoints/" +
                "PromoteComponentEndpoints.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void promote_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("apiVersionId", versionId);
        params.put("sourceReleaseId", releaseId);
        params.put("targetEnvironmentId", targetEnvironmentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "promote.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getComponentCountByOrgId_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetComponentCountByOrgId = "/component-mgt/1.0.0/orgs/" + orgId + "/component-count";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetComponentCountByOrgId, accessToken);
    }

    @Test
    @CitrusTest
    public void getProxyDeployment_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("orgUuid", orgUuid);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("environmentId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "getProxyDeploymentDetails.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void handleConfigInit_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "handleConfigInit.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void buildLogs_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("runID", runID);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "buildLogs.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void updateComponentEndpoint_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("releaseId", releaseId);
        params.put("endpointId", endpointId);
        params.put("displayName", displayName);
        params.put("apiContext", apiContext);
        params.put("apiDefinitionPath", apiDefinitionPath);
        params.put("visibility", visibility);
        String body = MessageUtils.generateStringFromTemplate("templates/endpoints/" +
                "UpdateEndpoint.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void runPod_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForRunPod = "/component-mgt/1.0.0/orgs/" + gitOrgHandle + "/projects/" + projectId +
        "/components/" + componentId + "/releases/" + releaseId + "/run-pod";
        String body = "";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrlForRunPod, body,
                accessToken);
    }

    @Test
    @CitrusTest
    public void configurableGenerationTrigger_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForConfigurableGenerationTrigger = "/component-mgt/1.0.0/orgs/" + gitOrgHandle +
                "/projects/" + projectId + "/triggers/configurable-generation";
        String body = "";
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient,
                requestUrlForConfigurableGenerationTrigger, body, accessToken);
    }

    @Test
    @CitrusTest
    public void getActionRunLogs_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetActionRunLogs = "/component-mgt/1.0.0/orgs/" + gitOrgHandle + "/projects/" +
                projectId + "/components/" + componentId + "/runs/" + runID + "/logs";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient, requestUrlForGetActionRunLogs,
                accessToken);
    }

    @Test
    @CitrusTest
    public void getComponentInitStatus_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetComponentInitStatus = "/component-mgt/1.0.0/orgs/" + gitOrgHandle + "/projects/" +
                projectId + "/components/" + componentId + "/init/status";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetComponentInitStatus, accessToken);
    }

    @Test
    @CitrusTest
    public void getFileContent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String requestUrlForGetComponentInitStatus = "/component-mgt/1.0.0/orgs/" + gitOrgHandle + "/projects/" +
                projectId + "/components/" + componentId + "/file/content?commitHash=" + commitHash +
                "&dirPath=";
        SecurityUtils.elevatedAccessCheckForGetRequests(this, choreoCPTestClient,
                requestUrlForGetComponentInitStatus, accessToken);
    }

    @Test
    @CitrusTest
    public void componentEndpointApiDefinition_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("endpointId", endpointId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "componentEndpointApiDefinition.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteComponent_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("endpointId", endpointId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "deleteComponent.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void deleteProject_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgId", orgId);
        params.put("projectId", projectId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "deleteProject.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }

    @Test
    @CitrusTest
    public void build_ComponentManagementElevatedAccessCheck() throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> params = new HashMap<>();
        params.put("orgHandler", orgHandler);
        params.put("buildId", buildId);
        params.put("componentId", componentId);
        params.put("versionId", versionId);
        params.put("commitHash", commitHash);
        params.put("devEnvId", envId);
        String body = MessageUtils.generateStringFromTemplate("templates/graphql/requests/" +
                "build.mustache", params);
        SecurityUtils.elevatedAccessCheckForPostRequests(this, choreoCPTestClient, requestUrl, body, accessToken);
    }
}
