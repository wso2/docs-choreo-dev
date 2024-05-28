/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

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
