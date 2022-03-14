package com.wso2.choreo.integration.common.choreoproject;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to represent an image of a container image
 */
public class Image {
    private String imageVersion;
    private String imageName;
    private String imageRegistry;
    private String imageRegistryId;
    private String tagName;
    private List<String> tags = new ArrayList<>();
    private String committer;
    private String commitMsg;
    private String gitHash;

    public String getImageVersion() {
        return imageVersion;
    }

    public void setImageVersion(String imageVersion) {
        this.imageVersion = imageVersion;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageRegistry() {
        return imageRegistry;
    }

    public void setImageRegistry(String imageRegistry) {
        this.imageRegistry = imageRegistry;
    }

    public String getImageRegistryId() {
        return imageRegistryId;
    }

    public void setImageRegistryId(String imageRegistryId) {
        this.imageRegistryId = imageRegistryId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public String getCommitMsg() {
        return commitMsg;
    }

    public void setCommitMsg(String commitMsg) {
        this.commitMsg = commitMsg;
    }

    public String getGitHash() {
        return gitHash;
    }

    public void setGitHash(String gitHash) {
        this.gitHash = gitHash;
    }
}
