package com.wso2.choreo.integration.apis.apimanager;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.ApiDTO;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIWrapper;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.revision.RevisionWrapper;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

public class ApiManager extends ControlPlaneAPI {

    private static final String APIS_ENDPOINT = STS_ENDPOINT + Constant.APIS_ENDPOINT;

    public static ProxyAPI createApiProxy(TestActionRunner runner, HttpClient client, String accessToken, String apiName) throws IOException {
        String resource = Constant.APIS_ENDPOINT.concat("?").concat(Constant.ORGANIZATION_ID).concat("=") + ORG_UUID;

        String apiContext = ORG_UUID.concat("/").concat(ORG_HANDLE).concat("/").concat(apiName.toLowerCase());
        String scopePrefix = "urn:" + ORG_HANDLE + ":" + apiName.toLowerCase() + ":";
        ApiDTO api = ApiDTO.builder().apiName(apiName).version(Constant.DEFAULT_VERSION).context(apiContext).
                scopePrefix(scopePrefix).productionEndpoint(Constant.DEFAULT_ENDPOINT).sandboxEndpoint(Constant.DEFAULT_ENDPOINT).build();
        String requestBody = ObjectMapperUtil.mapObjectToString("templates/api-proxy/requestBodyForAPICreation.mustache", api);

        AtomicReference<ProxyAPI> proxyAPI = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(5000)
                .actions(
                    http()
                            .client(client)
                            .send()
                            .post(resource)
                            .message()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .body(requestBody)
                            .accept(MediaType.APPLICATION_JSON_VALUE),
                    http()
                            .client(client)
                            .receive()
                            .response(HttpStatus.CREATED)
                            .message()
                            .type(MessageType.JSON)
                            .validate((message, context) -> {
                                proxyAPI.set(ObjectMapperUtil.mapStringToObject(ProxyAPI.class, message.getPayload(String.class), ""));
                            })));

        return proxyAPI.get();
    }


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

    public static KeyData getApiKey(TestActionRunner runner, HttpClient client, String accessToken, String apiId, String keyType) {
        String resource = Constant.APIS_ENDPOINT + "/" + apiId + "/generate-key?organizationId=" + ORG_UUID +
                "&keyType=" + keyType;

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

    public static JsonObject getApi(TestActionRunner runner, HttpClient client, String accessToken, String apiId) {
        String path = String.format( "%s/%s?organizationId=%s", Constant.APIS_ENDPOINT, apiId, ORG_UUID);
        AtomicReference<JsonObject> apiInfo = new AtomicReference<>();
        runner.$(http()
                .client(client)
                .send()
                .get(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON)
                .body(new ClassPathResource("templates/apimanager/responses/getApiSuccess.mustache"))
                .validate((message, context) -> {
                    String payload = message.getPayload(String.class);
                    JsonObject dataJsonObject = new JsonParser().parse(payload).getAsJsonObject();
                    apiInfo.set(dataJsonObject);
                }));

        return  apiInfo.get();
    }

    public static void updateApi(TestActionRunner runner, HttpClient client, String accessToken, String apiId, JsonObject reqBody) {
        String body = reqBody.toString();
        String path = String.format( "%s/%s?organizationId=%s", Constant.APIS_ENDPOINT, apiId, ORG_UUID);

        runner.$(http()
                .client(client)
                .send()
                .put(path)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .accept(MediaType.APPLICATION_JSON_VALUE));
        runner.$(http()
                .client(client)
                .receive()
                .response(HttpStatus.OK)
                .message()
                .type(MessageType.JSON));

    }

    public static ProxyAPIWrapper searchAPIByQuery(TestNGCitrusSpringSupport runner, HttpClient client, String accessToken, String query) throws IOException {
        String resource = Constant.APIS_ENDPOINT.concat("?").concat(Constant.ORGANIZATION_ID).concat("=") + ORG_UUID + "&query=" + query;
        AtomicReference<ProxyAPIWrapper> proxyWrapper = new AtomicReference<>();
        runner.$(repeatOnError()
                .until("i = 10")
                .index("i")
                .autoSleep(25000)
                .actions(
                    http()
                            .client(client)
                            .send()
                            .get(resource)
                            .message()
                            .header(HttpHeaders.AUTHORIZATION, accessToken)
                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE),
                    http()
                            .client(client)
                            .receive()
                            .response(HttpStatus.CREATED)
                            .message()
                            .type(MessageType.JSON)
                            .validate((message, context) -> {
                                String payload = message.getPayload(String.class);
                                proxyWrapper.set(ObjectMapperUtil.mapStringToObject(ProxyAPIWrapper.class, payload, ""));
                                if (proxyWrapper.get().getCount() == 0) {
                                    throw new ValidationException("No APIs found");
                                }
                            })));

        return proxyWrapper.get();
    }
}
