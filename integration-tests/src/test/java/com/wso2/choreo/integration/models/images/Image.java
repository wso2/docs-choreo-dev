package com.wso2.choreo.integration.models.images;


import lombok.Data;

@Data
public class Image {
    
    private String imageId;
    private String createdAt;
    private String updatedAt;
    private String commitHash;
    private String commitMessage;
       
}