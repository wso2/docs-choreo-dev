package com.wso2.choreo.integration.models.resourceAuthorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupWithUsersDTO {
    private String id;
    private String orgName;
    private String orgUuid;
    private String description;
    private String displayName;
    private String handle;
    private List<User> users;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
    private String uuid;

    @Data
    @NoArgsConstructor
    public static class User {
        private String id;
        private String idpId;
        private String pictureUrl;
        private String email;
        private String displayName;
    }
}
