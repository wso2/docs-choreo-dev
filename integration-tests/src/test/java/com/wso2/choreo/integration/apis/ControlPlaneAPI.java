package com.wso2.choreo.integration.apis;

import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;


public abstract class ControlPlaneAPI {

    protected static final String CHOREO_PROJECT_URL = Configuration.getConfig(ConfigDefinition.CHOREO_CP_PROJECTS_ENDPOINT) + Constant.GRAPHQL_ENDPOINT_SUFFIX;
    protected static final String ORG_HANDLE = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
    protected static final int ORG_ID = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
    protected static final String ORG_UUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

    protected static final String STS_ENDPOINT = Configuration.getConfig(ConfigDefinition.STS_ENDPOINT);
    protected static final String CHOREO_EP = Configuration.getConfig(ConfigDefinition.CHOREO_ENDPOINT);

    protected static final String GH_URL = Configuration.getConfig(ConfigDefinition.GITHUB_ENDPOINT);
    protected static final String GH_ORG = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
    protected static final String AUTH_HEADER = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(Configuration.getConfig(ConfigDefinition.GITHUB_PAT));

//    protected static final String AUTH_HEADER = Constant.GITHUB_AUTH_HEADER_PREFIX.concat("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");
//    protected static String GH_ORG="dasunorg";
//    protected   static String ORG_HANDLE = "dasunatwso2com";
//    protected   static int ORG_ID = 869;
//    protected   static String ORG_UUID = "fec0832e-94dd-4749-aa0f-7da5ed9e0a31";

    public static ChoreoOrganization getOrg() {
        return new ChoreoOrganization(ORG_HANDLE, ORG_ID, ORG_UUID);
    }

}
