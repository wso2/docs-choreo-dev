/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.marketplace;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.marketplace.ServiceInfo;
import com.wso2.choreo.integration.models.marketplace.ServiceVisibility;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class MarketplaceService {

    private static final String CONTEXT = "marketplace/0.1.0";

    public static List<ServiceInfo> searchForServices(TestNGCitrusSpringSupport runner, HttpClient client, String accessToken,
                                                      String serviceName, String networkVisibilityFilter) throws IOException {
        String encodedServiceName = URLEncoder.encode(serviceName, StandardCharsets.UTF_8);
        String encodedNetworkVisibilityFilter = URLEncoder.encode(networkVisibilityFilter, StandardCharsets.UTF_8);
        String searchServicesURL = CONTEXT.concat("/services").concat("?")
                .concat("limit=20&offset=0").concat("&")
                .concat("networkVisibilityFilter=").concat(encodedNetworkVisibilityFilter).concat("&")
                .concat("query=").concat(encodedServiceName);
        List<ServiceInfo> services = new ArrayList<ServiceInfo>();

        runner.variable("isServiceFound", false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isServiceFound} = true )")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(searchServicesURL)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code == HttpStatus.OK.value()) {
                                        ServiceInfo[] serviceArray = ObjectMapperUtil.mapDataToCollection(ServiceInfo[].class,
                                                message.getPayload(String.class), "data");
                                        if (serviceArray.length > 0) {
                                            context.setVariable("isServiceFound", true);
                                            services.addAll(List.of(serviceArray));
                                        }else{
                                                throw new ValidationException("Too many successive calls with empty response");                                        
                                        }
                                    }else{
                                        throw new ValidationException("Too many successive calls with response code != 200");                                       
                                    }
                                })
                )
        );
        return services;
    }

    public static String getChoreoServiceIdentifier(TestActionRunner runner, HttpClient client, String accessToken,
                                                    String serviceId, ServiceVisibility visibility) {
        String resourceURL = CONTEXT.concat("/services/").concat(serviceId).
                concat("/dependencyId").concat("?visibility=").concat(visibility.toString());
        AtomicReference<String> serviceIdentifier = new AtomicReference<>();
        runner.$(http()
                .client(client)
                .send()
                .get(resourceURL)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http().client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .validate((message, context) -> {
                            serviceIdentifier.set(message.getPayload(String.class));
                        }
                )
        );
        return serviceIdentifier.get();
    }
}
