package com.wso2.choreo.integration.apis.external;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpHeaders;


public class ManagedAuth {
    public static String initiateManagedAuthLoginFlow(String webAppUrl) throws ClientProtocolException, IOException {

        CloseableHttpClient instance = HttpClients.custom().disableRedirectHandling().build();

        final HttpGet httpGet = new HttpGet(webAppUrl + "/auth/login");
        CloseableHttpResponse response = instance.execute(httpGet);

        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, response.getStatusLine().getStatusCode());

        return response.getFirstHeader(HttpHeaders.LOCATION).getValue();
    }
}
