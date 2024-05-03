package com.wso2.choreo.integration.models.resourceAuthorization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleList {
    private String count;
    private List<Role> list;
    @JsonIgnoreProperties(ignoreUnknown = true)
    private Pagination pagination;

    @Data
    @NoArgsConstructor
    public static class Pagination {
        private String limit;
        private String total;
    }
}
