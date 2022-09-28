package com.wso2.choreo.integration.tests.jwt;

import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.TestContext;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import java.util.Base64;


public class TestClientJwTValidation extends TestNGCitrusSpringSupport {


    private String userTokenHeader;
    private String userTokenPayload;

    private String stsTokenHeader;
    private String stsTokenPayload;

    private String stsToken;
    private String access_token;

    String jwtHeader;
    String jwtPayload;
    @Autowired
    private HttpClient choreoTestClientForAsgardeo;
    @Autowired
    private HttpClient choreoTestClientForSTS;


    @BeforeClass
    public void setup()  {

        userTokenHeader = TestContext.getTestUserTokenHandler().getEncodedCredentials(Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_ID),
                Configuration.getConfig(ConfigDefinition.ASGARDEO_CLIENT_SECRET));
        userTokenPayload = TestContext.getTestUserTokenHandler().getUserTokenPayload();
        stsTokenHeader = TestContext.getTestUserTokenHandler().getEncodedCredentials(Configuration.getConfig(ConfigDefinition.STS_CLIENT_ID),
                Configuration.getConfig(ConfigDefinition.STS_CLIENT_SECRET));
    }

    @Test
    @CitrusTest
    public void testValidateJWT() {

        $(http().client(choreoTestClientForAsgardeo).
                send().
                post(Constant.TOKEN_ENDPOINT_SUFFIX).
                message().
                header(HttpHeaders.AUTHORIZATION, userTokenHeader)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE).
                body(userTokenPayload));


        $(http().client(choreoTestClientForAsgardeo).
                receive().
                response().
                message().
                type(MessageType.JSON).
                validate(((message, testContext) -> {
                    JsonObject component = new JsonParser().parse((String) message.getPayload()).getAsJsonObject();
                    access_token = component.get("access_token").getAsString();
                })));

        stsTokenPayload = TestContext.getTestUserTokenHandler().getStsTokenPayload(access_token);


        $(http().client(choreoTestClientForSTS).
                send().
                post(Constant.TOKEN_ENDPOINT_SUFFIX).
                message().
                header(HttpHeaders.AUTHORIZATION, stsTokenHeader)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE).
                body(stsTokenPayload));


        $(http().client(choreoTestClientForSTS).
                receive().
                response().
                message().
                type(MessageType.JSON).
                validate(((message, testContext) -> {
                    JsonObject component = new JsonParser().parse(message.getPayload().toString()).getAsJsonObject();
                    stsToken = component.get("access_token").getAsString();
                    System.out.println(stsToken);
                    String[] tokeValues = stsToken.split("\\.");

                    Base64.Decoder decoder = Base64.getDecoder();
                    jwtHeader = new String(decoder.decode(tokeValues[0]));
                    jwtPayload = new String(decoder.decode(tokeValues[1]));

                    JsonObject jsonObject = new JsonParser().parse(jwtPayload).getAsJsonObject();
                    String aut = jsonObject.get("aut").getAsString();
                    String scope = jsonObject.get("scope").getAsString();
                    Assert.assertEquals(aut, "APPLICATION_USER");
                    Assert.assertEquals(scope, "default");

                })));


    }
}

