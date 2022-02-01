package com.wso2.choreo.integration.common.choreoproject;

/**
 * A class to represent container image of a deployment history
 */
public class ContainerImage {
    private String imageId;
    private String containerId;
    private Image image;

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
