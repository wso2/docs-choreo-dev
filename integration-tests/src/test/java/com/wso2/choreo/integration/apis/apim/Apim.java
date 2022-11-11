package com.wso2.choreo.integration.apis.apim;

import com.wso2.choreo.integration.apis.AbstractConfigs;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class Apim extends AbstractConfigs {

    private static  final String APIM_ENDPOINT = STS_ENDPOINT + Constant.API_VALIDATE_ENDPOINT;
    public static Response validateAPIName(String apiName, String accessToken) throws IOException {
        String requestURL = APIM_ENDPOINT.concat("?").concat(Constant.ORGANIZATION_ID)
                .concat("=").concat(ORG_UUID)
                .concat("&query=name:").concat(apiName);
        log.info(requestURL);
        return HttpClientUtil.httpPOST(requestURL, "", accessToken, "");

    }


}
