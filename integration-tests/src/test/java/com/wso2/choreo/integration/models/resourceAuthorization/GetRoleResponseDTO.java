/*
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

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
public class GetRoleResponseDTO {
    private String id;
    private String description;
    private String displayName;
    private String handle;
    private boolean defaultRole;
    private List<Tag> tags;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;
    private String uuid;

    @Data
    @NoArgsConstructor
    public static class Tag {
        private String handle;
        private String createdAt;
        private String updatedAt;
    }
}
