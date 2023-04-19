package com.wso2.choreo.integration.apis.apimanager;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.revision.Revision;
import com.wso2.choreo.integration.models.revision.RevisionWrapper;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import org.springframework.beans.factory.annotation.Autowired;
import com.wso2.choreo.integration.common.Endpoints;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.http.actions.HttpActionBuilder.http;


public class ApiManager extends ControlPlaneAPI {

    private static final String APIS_ENDPOINT = STS_ENDPOINT + Constant.APIS_ENDPOINT;

    @Autowired
    static
    Map<Endpoints, HttpClient> citrusClients;
    @Autowired
    private static HttpClient choreoTestClientForSTS;


    public static Response changeLifeCycle(String apiId, String action, String accessToken) throws IOException {
        String url = STS_ENDPOINT + "api/am/publisher/v2/apis/change-lifecycle?organizationId=" + ORG_UUID + "&apiId=" + apiId + "&action=" + action;
        return HttpClientUtil.httpPOST(url, "", accessToken, "");
    }


    public static RevisionWrapper getApiRevision(String apiId, String accessToken) {

        String requestURI = Configuration.getConfig(ConfigDefinition.STS_ENDPOINT)
                .concat("/api/am/publisher/v2/apis/")
                .concat(apiId).concat("/revisions?organizationId=")
                .concat(ORG_UUID);

        Response res = HttpClientUtil.httpGET(requestURI, accessToken, "");
        return ObjectMapperUtil.mapStringToObject(RevisionWrapper.class, res.getRes(), "");

    }

    public static KeyData getApiKey(TestActionRunner runner, HttpClient client, String accessToken, String apiId) {
        String resource = Constant.APIS_ENDPOINT + "/" + apiId + "/generate-key?organizationId=" + ORG_UUID;

        AtomicReference<KeyData> keyData = new AtomicReference<>();

        runner.$(http()
                .client(client)
                .send()
                .post(resource)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/apimanager/responses/generateKeySuccess.json"))
                .validate((message, context) -> {
                    keyData.set(ObjectMapperUtil.mapStringToObject(KeyData.class, message.getPayload(String.class), ""));
                }));

       return  keyData.get();
    }

    public static RevisionWrapper getRevisionCount(TestActionRunner runner,HttpClient client, String accessToken, String apiId,String orgUuid) throws Exception {

        AtomicReference<RevisionWrapper> revisionWrapper = new AtomicReference<>();
        String path = Constant.APIS_ENDPOINT.concat("/").concat(apiId)
                .concat("/").concat("revisions")
                .concat("?").concat(Constant.ORGANIZATION_ID).concat("=").concat(orgUuid);

        runner.$(http()
                .client(client)
                .send()
                .get(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(String.valueOf(MediaType.APPLICATION_JSON)));

        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/maxApiRevisions/get_revisions_success.mustache"))
                .validate((message, context) -> {
                    revisionWrapper.set(ObjectMapperUtil.mapStringToObject(RevisionWrapper.class, message.getPayload(String.class), ""));
                }));
        RevisionWrapper data = revisionWrapper.get();
        return data;



    }
}
