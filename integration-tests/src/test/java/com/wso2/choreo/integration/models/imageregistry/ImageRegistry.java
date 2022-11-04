package com.wso2.choreo.integration.models.imageregistry;



import lombok.Data;



@Data

public class ImageRegistry {

    private String id;
    private String createdAt;
    private String updatedAt;
    private String cloudConnectorId;
    private String imageRepositoryName;
}
