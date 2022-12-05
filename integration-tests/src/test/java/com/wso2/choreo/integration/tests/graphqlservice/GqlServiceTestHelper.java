package com.wso2.choreo.integration.tests.graphqlservice;

import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
@Slf4j
public class GqlServiceTestHelper {

    public static Response sendRequest(String invokeURL, String payload, String apiKey) throws IOException {
        log.info(invokeURL);
        String url = invokeURL+"/";
        String request = ObjectMapperUtil.mapToGraphQLQuery(payload);
        return HttpClientUtil.httpPOST(url, request, "", apiKey);

    }
}
