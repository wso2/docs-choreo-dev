package com.wso2.choreo.integration.common.choreoproject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wso2.choreo.integration.common.ChoreoOrganization;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.common.exceptions.GraphQLException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Class to represent Choreo project
 */

@Data
public class ChoreoProject {

    private static final Logger log = LogManager.getLogger(ChoreoProject.class);
    private static final Gson gson = new Gson();

    private final HashMap<String, ChoreoComponent> componentMap = new HashMap<>();
    private String id;
    private String version;
    private String orgId;
    private String labels;
    private String handler;
    private String extendedHandler;
    private String name;
    private String description;
    private String createdDate;
    private String region;
    private String GitRepoURL = "https://github.com/choreo-test-apps/rest-api";

    private String getComponentsQuery() {

        return "query{" +
                "      components(" +
                "        orgHandler: \"" + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE) + "\"," +
                "        projectId: \"" + id + "\"" +
                "      ){\n" +
                "        projectId," +
                "        id," +
                "        description," +
                "        name," +
                "        handler," +
                "        displayName," +
                "        displayType," +
                "        version," +
                "        createdAt," +
                "        orgHandler" +
                "      }" +
                "    }";
    }

    public String getDeleteComponentMutation(String componentId) {
        return "mutation{ deleteComponentV2(" +
                "        orgHandler: \"" + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE) + "\"," +
                "        projectId: \"" + id + "\",\n" +
                "        componentId: \"" + componentId + "\"){status, canDelete, message" +
                "}}";
    }

    public List<ChoreoComponent> getComponents(String accessToken) {
        List<ChoreoComponent> components = new ArrayList<>();
        String gqlQuery = getComponentsQuery();

        try {
            JsonObject body = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

            JsonArray componentsJson = body.getAsJsonObject().getAsJsonObject("data")
                    .getAsJsonArray("components");

            for (int i = 0; i < componentsJson.size(); ++i) {
                JsonObject componentJson = componentsJson.get(i).getAsJsonObject();

                components.add(gson.fromJson(componentJson.toString(), (Type) RestApiChoreoComponent.class));
            }
        } catch (GraphQLException e) {
            log.error("Error while retrieving components", e);
        }

        return components;
    }

    public void deleteComponent( String accessToken, String componentId) {
        String gqlQuery = getDeleteComponentMutation(componentId);

        try {
            ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

        } catch (GraphQLException e) {
            log.error("Error while deleting component", e);
        }
    }
}
