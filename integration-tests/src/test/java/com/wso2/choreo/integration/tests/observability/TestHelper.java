package com.wso2.choreo.integration.tests.observability;

import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.models.response.Response;

import java.util.concurrent.ThreadLocalRandom;

public class TestHelper {

    public static String getExpectedResponse() {
        return "true";
    }

    public static void invokeEP(String invokeURL, String apiKey) {
        String url = invokeURL + "/isOdd?number=12121";
        int rand = ThreadLocalRandom.current().nextInt(1, 5);
        for (int i = 0; i < rand; i++) {
            HttpClientUtil.httpGET(url, "", apiKey);
        }

    }
}
