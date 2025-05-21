package com.wso2.choreo.integration.apis.platformServices;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.actions.HttpActionBuilder;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.wso2.choreo.integration.models.platformServices.CloudProvider;
import com.wso2.choreo.integration.models.platformServices.CloudRegion;
import com.wso2.choreo.integration.models.platformServices.DBServerPowerAction;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.platformServices.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.http.message.HttpMessageHeaders.HTTP_STATUS_CODE;


public class PlatformServices {
    public static CreatedDatabaseServer createDatabaseServer(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerName,
                                                             String servicePlanId, String accessToken) {
        String resource = Constant.PSM_SUFFIX;
        DatabaseServerCreateRequest dbServerReq = DatabaseServerCreateRequest.builder()
                .name(dbServerName)
                .service_plan_id(servicePlanId)
                .cloud_provider(CloudProvider.AWS.getValue())
                .cloud_region(CloudRegion.US.getValue())
                .is_vector_enabled(false)
                .build();
        String requestPayload = ObjectMapperUtil.mapObjectToString(dbServerReq);
        AtomicReference<CreatedDatabaseServer> dbServer = new AtomicReference<>();
        runner.variable("isSuccess", false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isSuccess} = true )")
                .index("i")
                .autoSleep(5000)
                .actions(
                        HttpActionBuilder.http()
                                .client(client)
                                .send()
                                .post(resource)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .body(requestPayload),
                        HttpActionBuilder.http()
                                .client(client)
                                .receive()
                                .response()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.CREATED.value()) {
                                        throw new ValidationException("Database server creation failed with status code: " + code + " error:" + message.getPayload(String.class));
                                    }
                                    dbServer.set(ObjectMapperUtil.mapStringToObject(CreatedDatabaseServer.class, message.getPayload(String.class), ""));
                                })));
        return dbServer.get();

    }

    public static Database createDatabase(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerId, String databaseName,
                                          String accessToken) {
        String resource = Constant.PSM_SUFFIX.concat("/").concat(dbServerId).concat("/databases");
        DatabaseCreateRequest dbCreateReq = DatabaseCreateRequest.builder()
                .name(databaseName)
                .build();
        String requestPayload = ObjectMapperUtil.mapObjectToString(dbCreateReq);
        AtomicReference<Database> database = new AtomicReference<>();
        runner.variable("isSuccess", false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isSuccess} = true )")
                .index("i")
                .autoSleep(10000)
                .actions(
                        HttpActionBuilder.http()
                                .client(client)
                                .send()
                                .post(resource)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .body(requestPayload),
                        HttpActionBuilder.http()
                                .client(client)
                                .receive()
                                .response()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Database creation failed with status code: " +  + code + " error:" + message.getPayload(String.class));
                                    }
                                    database.set(ObjectMapperUtil.mapStringToObject(Database.class, message.getPayload(String.class), ""));
                                })));
        return database.get();
    }

    public static DatabaseCredentials createDatabaseCredentials(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerId, String databaseName,
                                                                List<String> envIds, String accessToken) {
        String resource = Constant.PSM_SUFFIX.concat("/").concat(dbServerId).concat("/credentials");
        DatabaseCredentialsCreateRequest dbCredCreateReq = DatabaseCredentialsCreateRequest.builder()
                .database(databaseName)
                .display_name(databaseName.concat("-").concat(String.valueOf(System.currentTimeMillis())))
                .is_super_admin(true)
                .applicable_environments(envIds)
                .build();

        String requestPayload = ObjectMapperUtil.mapObjectToString(dbCredCreateReq);
        AtomicReference<DatabaseCredentials> credential = new AtomicReference<>();
        runner.variable("isSuccess", false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isSuccess} = true )")
                .index("i")
                .autoSleep(30000)
                .actions(
                        HttpActionBuilder.http()
                                .client(client)
                                .send()
                                .post(resource)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .body(requestPayload),
                        HttpActionBuilder.http()
                                .client(client)
                                .receive()
                                .response()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.CREATED.value()) {
                                        throw new ValidationException("Database credential creation failed with status code: " +  + code + " error:" + message.getPayload(String.class));
                                    }
                                    credential.set(ObjectMapperUtil.mapStringToObject(DatabaseCredentials.class, message.getPayload(String.class), ""));
                                })));
        return credential.get();
    }

    public static Database UpdateDatabaseMarketplaceStatus(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerId, String databaseName,
                                                           boolean isAddingToMarketplace, String accessToken) throws ValidationException {
        String resource = Constant.PSM_SUFFIX.concat("/").concat(dbServerId).concat("/databases/").concat(databaseName);
        Database marketPlaceReq = Database.builder()
                .name(databaseName)
                .display_on_marketplace(isAddingToMarketplace)
                .build();
        String requestPayload = ObjectMapperUtil.mapObjectToString(marketPlaceReq);
        AtomicReference<Database> database = new AtomicReference<>();
        runner.variable("isSuccess", false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isSuccess} = true )")
                .index("i")
                .autoSleep(10000)
                .actions(
                        HttpActionBuilder.http()
                                .client(client)
                                .send()
                                .put(resource)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .body(requestPayload),
                        HttpActionBuilder.http()
                                .client(client)
                                .receive()
                                .response()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Database marketplace status update failed with status code: " +  + code + " error:" + message.getPayload(String.class));
                                    }
                                    database.set(ObjectMapperUtil.mapStringToObject(Database.class, message.getPayload(String.class), ""));
                                })));
        return database.get();
    }

    public static CreatedDatabaseServer UpdateDatabaseServerMarketplaceStatus(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerId, String dbServerName,
                                                                              boolean isAddingToMarketplace, String accessToken) throws ValidationException {
        String resource = Constant.PSM_SUFFIX.concat("/").concat(dbServerId);
        DatabaseServerPutRequest marketPlaceReq = DatabaseServerPutRequest.builder()
                .name(dbServerName)
                .service_plan_id(Constant.MYSQL_SERVICE_PLAN_ID)
                .cloud_provider(CloudProvider.AWS.getValue())
                .cloud_region(CloudRegion.US.getValue())
                .display_on_marketplace(isAddingToMarketplace)
                .build();
        String requestPayload = ObjectMapperUtil.mapObjectToString(marketPlaceReq);
        AtomicReference<CreatedDatabaseServer> databaseServer = new AtomicReference<>();
        runner.variable("isSuccess", false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isSuccess} = true )")
                .index("i")
                .autoSleep(10000)
                .actions(
                        HttpActionBuilder.http()
                                .client(client)
                                .send()
                                .put(resource)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .body(requestPayload),
                        HttpActionBuilder.http()
                                .client(client)
                                .receive()
                                .response()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Database server marketplace status update failed with status code: " +  + code + " error:" + message.getPayload(String.class));
                                    }
                                    databaseServer.set(ObjectMapperUtil.mapStringToObject(CreatedDatabaseServer.class, message.getPayload(String.class), ""));
                                })));
        return databaseServer.get();
    }

    public static void UpdateDatabaseServerPowerStatus(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerId,
                                                       DBServerPowerAction action, String accessToken) throws IOException {
        String resource = Constant.PSM_SUFFIX.concat("/").concat(dbServerId).concat("/power");
        DatabaseServerPowerPayload powerUpdateReq = DatabaseServerPowerPayload.builder()
                .action(action.getValue())
                .build();
        String requestPayload = ObjectMapperUtil.mapObjectToString(powerUpdateReq);
        AtomicReference<CreatedDatabaseServer> databaseServer = new AtomicReference<>();
        runner.variable("isSuccess", false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isSuccess} = true )")
                .index("i")
                .autoSleep(10000)
                .actions(
                        HttpActionBuilder.http()
                                .client(client)
                                .send()
                                .put(resource)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .body(requestPayload),
                        HttpActionBuilder.http()
                                .client(client)
                                .receive()
                                .response()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Database server power update failed with status code: " + code + " error:" + message.getPayload(String.class));
                                    }
                                })));
    }

    public static List<CreatedDatabaseServer> getDatabaseServers(TestNGCitrusSpringSupport runner, HttpClient client,
                                                                 String orgUUID, String accessToken) {
        String resource = Constant.PSM_SUFFIX.concat("?organization_id=").concat(orgUUID);
        AtomicReference<List<CreatedDatabaseServer>> dbServers = new AtomicReference<>();
        runner.$(http()
                .client(client)
                .send()
                .get(resource)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http().client(client)
                .receive()
                .response()
                .message()
                .validate((message, context) -> {
                            int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                            if (code != HttpStatus.OK.value()) {
                                throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                            }
                            try {
                                dbServers.set(new ObjectMapper()
                                        .readValue(message.getPayload().toString(),
                                                new TypeReference<List<CreatedDatabaseServer>>() {
                                                }));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
        return dbServers.get();
    }

    public static DatabaseServer getDatabaseServer(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerId,
                                                          String orgUUID, String accessToken) {
        String resource = Constant.PSM_SUFFIX.concat("/").concat(dbServerId).concat("?organization_id=").concat(orgUUID);;
        AtomicReference<DatabaseServer> dbServer = new AtomicReference<>();
        runner.$(http()
                .client(client)
                .send()
                .get(resource)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http().client(client)
                .receive()
                .response()
                .message()
                .validate((message, context) -> {
                            int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                            if (code != HttpStatus.OK.value()) {
                                throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                            }
                            dbServer.set(ObjectMapperUtil.mapStringToObject(DatabaseServer.class, message.getPayload(String.class), ""));
                        }
                )
        );
        return dbServer.get();
    }

    public static Boolean validateDatabaseServerPowerStatus(TestActionRunner runner, HttpClient client, String dbServerId, ServerStatus expectedStatus,
                                                         String accessToken) {

        String resource = Constant.PSM_SUFFIX.concat("/").concat(dbServerId);
        AtomicReference<Boolean> isPowerStatusValid = new AtomicReference<>();

        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(60000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(resource)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http().client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException("Database server power status retrieval failed with status code: " + code + " error:" + message.getPayload(String.class));
                                    }
                                    String status = JsonPath.parse(message.getPayload(String.class))
                                            .read("$.status", String.class);
                                    if (status.equals(expectedStatus.toString())) {
                                        isPowerStatusValid.set(true);
                                    } else {
                                        isPowerStatusValid.set(false);
                                        throw new ValidationException("Database power status does not match the required status");

                                    }
                                })
                )
        );
        return isPowerStatusValid.get();
    }

    public static List<Database> getDatabases(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerId,
                                              String accessToken) {
        String resource = Constant.PSM_SUFFIX.concat("/").concat(dbServerId).concat("/databases");
        AtomicReference<List<Database>> databases = new AtomicReference<>();

        runner.$(http()
                .client(client)
                .send()
                .get(resource)
                .message()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE));

        runner.$(http().client(client)
                .receive()
                .response()
                .message()
                .validate((message, context) -> {
                            int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                            if (code != HttpStatus.OK.value()) {
                                throw new ValidationException("Unexpected HTTP Response Status Code: " + code);
                            }
                            try {
                                databases.set(new ObjectMapper()
                                        .readValue(message.getPayload().toString(),
                                                new TypeReference<List<Database>>() {
                                                }));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ));
        return databases.get();
    }

    public static void DeleteDatabaseCredentials(TestNGCitrusSpringSupport runner, HttpClient client, String dbServerId, String credentialId,
                                                 String accessToken) {
        String resource = Constant.PSM_SUFFIX.concat("/").concat(dbServerId).concat("/credentials/").concat(credentialId);
        runner.$(repeatOnError()
                .until("i = 5")
                .index("i")
                .autoSleep(10000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(resource)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(MediaType.APPLICATION_JSON_VALUE),
                        http().client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value() &&  code != HttpStatus.NOT_FOUND.value()) {
                                        throw new ValidationException("Database credential deletion failed with status code: " + code + " error:" + message.getPayload(String.class));
                                    }
                                })
                )
        );
    }

}
