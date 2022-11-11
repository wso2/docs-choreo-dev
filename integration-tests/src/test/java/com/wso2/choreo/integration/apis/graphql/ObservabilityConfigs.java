package com.wso2.choreo.integration.apis.graphql;

import com.wso2.choreo.integration.apis.AbstractConfigs;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.Response;

import java.io.IOException;

public class ObservabilityConfigs extends AbstractConfigs {




    public static  void  getObservabilityEnvironments(String accessToken) throws IOException {
        GraphqlDTO dto = GraphqlDTO.builder().componentHandler(ORG_UUID).build();
        String expectedResponse = ObjectMapperUtil.mapObjectToString("templates/observability/graphql/queryForComponentEnvironmentInformation.mustache", dto);
        Response response = HttpClientUtil.httpPOST(CHOREO_PROJECT_URL, ObjectMapperUtil.mapToGraphQLQuery(expectedResponse), accessToken, "");
        System.out.println(response.getRes());
    //    return ObjectMapperUtil.mapStringToObject(RestApiChoreoComponent.class, response.getRes(), "component");

    }
}
