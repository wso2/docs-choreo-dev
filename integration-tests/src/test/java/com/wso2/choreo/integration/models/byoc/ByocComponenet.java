package com.wso2.choreo.integration.models.byoc;



import lombok.Data;



@Data

public class ByocComponenet {

    private  String id;
    private  String createdAt;
    private  String updatedAt;
    private  String name;
    private  String handle;
    private  String organizationId;
    private  String projectId;
    private  String orgHandle;
    private  String type;
    private  String description;
    private  String imageRegistryId;
    private  String componentType;
    private  boolean httpBased;
    private  ImageRegistry imageRegistry;


}
