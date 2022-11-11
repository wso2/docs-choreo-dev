package com.wso2.choreo.integration.apis;

import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;


public abstract class AbstractConfigs {

    public static final String CHOREO_PROJECT_URL = Configuration.getConfig(ConfigDefinition.CHOREO_CP_PROJECTS_ENDPOINT) + Constant.GRAPHQL_ENDPOINT_SUFFIX;
    public static final String ORG_HANDLE = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
    public static final int ORG_ID = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
    public static final String ORG_UUID = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

    public static  final  String STS_ENDPOINT =  Configuration.getConfig(ConfigDefinition.STS_ENDPOINT);
}
