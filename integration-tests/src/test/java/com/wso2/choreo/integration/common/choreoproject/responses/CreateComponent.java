package com.wso2.choreo.integration.common.choreoproject.responses;


public class CreateComponent {
    private String id;
    private int orgId;
    private String projectId;
    private String handler;

    public CreateComponent(String id, int orgId, String projectId, String handler) {
        this.id = id;
        this.orgId = orgId;
        this.projectId = projectId;
        this.handler = handler;
    }

    public String getId() {
        return id;
    }

    public int getOrgId() {
        return orgId;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getHandler() {
        return handler;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    @Override
    public String toString() {
        return "ComponentCreateResponse{" +
                "id='" + id + '\'' +
                ", orgId=" + orgId +
                ", projectId='" + projectId + '\'' +
                ", handler='" + handler + '\'' +
                '}';
    }
}
