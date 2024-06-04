package com.wso2.choreo.integration.models.devopsportalapi;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Image {
    private String ID;
    private String image_name_with_tag;
    @JsonProperty("trigger_source")
    private String triggerSource;
    private Object metadata;
    @JsonProperty("is_bal_image")
    private boolean isBalImage;
    @JsonProperty("image_ports")
    private Object imagePorts;
    @JsonProperty("api_version_id")
    private String apiVersionId;
    @JsonProperty("commit_msg")
    private String commitMsg;
    private String committer;
    @JsonProperty("project_id")
    private String projectId;
    @JsonProperty("built_at")
    private String builtAt;
    @JsonProperty("cluster_image_tags")
    private List<ClusterImageTag> clusterImageTags;
    @JsonProperty("run_id")
    private String runId;
    @JsonProperty("image_version")
    private Object imageVersion;
    @JsonProperty("image_registry_id")
    private String imageRegistryId;
    private List<String> tags;
    @JsonProperty("git_ops_hash")
    private String gitOpsHash;
    @JsonProperty("image_registry")
    private Object imageRegistry;
    @JsonProperty("platformer_tag")
    private String platformerTag;
    @JsonProperty("organization_id")
    private String organizationId;
    @JsonProperty("git_hash_commit_timestamp")
    private String gitHashCommitTimestamp;
    private String status;
    @JsonProperty("git_hash")
    private String gitHash;

    public String getImageNameWithTag() {
        return image_name_with_tag;
    }
}

@Data
class ClusterImageTag {
    @JsonProperty("registry_id")
    private String registryId;
    private Object clusters;
    @JsonProperty("image_name_with_tag")
    private String imageNameWithTag;
}
