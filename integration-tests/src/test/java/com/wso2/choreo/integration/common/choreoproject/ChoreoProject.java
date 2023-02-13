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

    /**
     * Create a Choreo component
     *
     * @param componentBuilder instance to create specific type of Choreo component
     * @param accessToken      OAuth token to invoke the Chorea backend
     * @return A Choreo component
     */
    public ChoreoComponent createChoreoComponent(String accessToken, AbstractChoreoComponentBuilder componentBuilder)
            throws ComponentCreationTimeoutException,
            ComponentCreationStatusCheckException, IOException, ComponentCreationException, ComponentRetrieveException,
            InterruptedException {
        ChoreoComponent choreoComponent = componentBuilder.createChoreoComponent(accessToken);
        componentMap.put(choreoComponent.getId(), choreoComponent);
        return choreoComponent;
    }


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

    private String getComponentQuery(String componentHandler) {
        return  "query{" +
                "      component(" +
                "        projectId: \"" + id + "\"" +
                "        componentHandler: \"" + componentHandler + "\"" +
                "      ){" +
                "        id," +
                "        name," +
                "        handler," +
                "        description," +
                "        displayType," +
                "        displayName," +
                "        ownerName," +
                "        orgId," +
                "        orgHandler," +
                "        version," +
                "        labels," +
                "        createdAt," +
                "        updatedAt," +
                "        projectId," +
                "        apiId," +
                "        repository{" +
                "          nameApp," +
                "          nameConfig," +
                "          branch," +
                "          organizationApp," +
                "          organizationConfig," +
                "          isUserManage" +
                "        }," +
                "        apiVersions{" +
                "          apiVersion," +
                "          proxyName," +
                "          proxyUrl," +
                "          proxyId," +
                "          id," +
                "          state," +
                "          latest," +
                "          branch," +
                "          appEnvVersions{" +
                "            environmentId," +
                "            releaseId," +
                "            release{" +
                "              id," +
                "              metadata{" +
                "                choreoEnv" +
                "              }," +
                "              environmentId," +
                "              environment," +
                "              gitHash," +
                "              gitOpsHash," +
                "            }" +
                "          }" +
                "        }" +
                "      }" +
                "    }";
    }

    public String getCreateRestAPIMutation(String componentName, ChoreoOrganization org) {
        return "mutation{ createComponent(" +
                "      component: {" +
                "        name: \"" + componentName + "\"," +
                "        orgId: " + org.getOrgId() + "," +
                "        orgHandler: \"" + org.getOrgHandle() + "\"," +
                "        displayName: \"" + componentName + "\"," +
                "        displayType: \"" + Constant.displayType.restAPI + "\"," +
                "        projectId: \"" + getId() + "\"," +
                "        labels: \"\"," +
                "        version: \"1.0.0\"," +
                "        description: \"\"," +
                "        apiId: \"\"," +
                "        ballerinaVersion: \"swan-lake-alpha5\"," +
                "        triggerChannels: \"\"," +
                "        triggerID: null," +
                "        httpBase: true," +
                "        sampleTemplate: \"\"" +
                "      }){" +
                "        id, orgId, projectId, handler" +
                "      }}";
    }






    public String getDeleteComponentMutation(String componentId) {
        return "mutation{ deleteComponentV2(" +
                "        orgHandler: \"" + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE) + "\"," +
                "        projectId: \"" + id + "\",\n" +
                "        componentId: \"" + componentId + "\"){status, canDelete, message" +
                "}}";
    }

    public ChoreoComponent createRestAPI(String accessToken, String componentName, ChoreoOrganization org)
            throws ComponentCreationException, ComponentCreationTimeoutException,
            ComponentCreationStatusCheckException, ComponentRetrieveException {
        String gqlQuery = getCreateRestAPIMutation(componentName, org);

        try {
            JsonObject response = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

            JsonObject choreoComponentJsonObject = response.getAsJsonObject("data").getAsJsonObject("createComponent");
            String componentId = choreoComponentJsonObject.get("id").isJsonNull() ? "" :
                    choreoComponentJsonObject.get("id").getAsString();
            String componentHandler = choreoComponentJsonObject.get("handler").isJsonNull() ? "" :
                    choreoComponentJsonObject.get("handler").getAsString();
            ControlPlaneAPIs.waitForComponentCreationSuccess(accessToken, org.getOrgHandle(), getId(), componentId);
            Optional<ChoreoComponent> component = getComponentByHandler(accessToken, componentHandler);

            if (component.isPresent()) {
                return component.get();
            }

            throw new ComponentRetrieveException("Could not find component with handler: " + componentHandler);
        } catch (GraphQLException e) {
            throw new ComponentCreationException(e);
        }
    }

    /**
     * Get details of created Choreo component
     *
     * @param name The name of the Choreo component
     */
    public Optional<ChoreoComponent> getComponentByName(String accessToken, String name) throws ComponentRetrieveException {
        String gqlQuery = getComponentsQuery();

        try {
            JsonObject body = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

            JsonArray components = body.getAsJsonObject().getAsJsonObject("data")
                    .getAsJsonArray("components");

            for (int i = 0; i < components.size(); ++i) {
                JsonObject componentJson = components.get(i).getAsJsonObject();

                String componentName = componentJson.get("name").isJsonNull() ? "" :
                        componentJson.get("name").getAsString();

                if (componentName.equals(name)) {
                    String componentHandler = componentJson.get("handler").isJsonNull() ? "" :
                            componentJson.get("handler").getAsString();

                    return getComponentByHandler(accessToken, componentHandler);
                }
            }
        } catch (GraphQLException e) {
            throw new ComponentRetrieveException(e);
        }

        return Optional.empty();
    }

    public Optional<ChoreoComponent> getComponentByHandler(String accessToken, String componentHandler) throws ComponentRetrieveException {
        String gqlQuery = getComponentQuery(componentHandler);

        try {
            JsonObject body = ControlPlaneAPIs.callGraphQL(accessToken, gqlQuery);

            JsonObject componentJson = body.getAsJsonObject().getAsJsonObject("data")
                    .getAsJsonObject("component");

            return Optional.of(gson.fromJson(componentJson.toString(), (Type) RestApiChoreoComponent.class));
        } catch (GraphQLException e) {
            throw new ComponentRetrieveException(e);
        }
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

    public HashMap<String, ChoreoComponent> getComponentMap() {
        return componentMap;
    }
}
