package com.wso2.choreo.integration.common.choreoproject;

import com.wso2.choreo.integration.common.exceptions.ComponentCreationException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationStatusCheckException;
import com.wso2.choreo.integration.common.exceptions.ComponentCreationTimeoutException;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import java.io.IOException;
import java.util.HashMap;

/**
 * Class to represent Choreo project
 */
public class ChoreoProject {

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

    /**
     * Create a Choreo component
     *
     * @param componentBuilder instance to create specific type of Choreo component
     * @param accessToken      OAuth token to invoke the Chorea backend
     * @return A Choreo component
     */
    public ChoreoComponent createChoreoComponent(AbstractChoreoComponentBuilder componentBuilder, String accessToken, String projectsAPIAcessToken)
            throws ComponentCreationTimeoutException,
            ComponentCreationStatusCheckException, IOException, ComponentCreationException, ComponentRetrieveException,
            InterruptedException {
        ChoreoComponent choreoComponent = componentBuilder.createChoreoComponent(accessToken, projectsAPIAcessToken);
        componentMap.put(choreoComponent.getId(), choreoComponent);
        return choreoComponent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getExtendedHandler() {
        return extendedHandler;
    }

    public void setExtendedHandler(String extendedHandler) {
        this.extendedHandler = extendedHandler;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public HashMap<String, ChoreoComponent> getComponentMap() {
        return componentMap;
    }
}
