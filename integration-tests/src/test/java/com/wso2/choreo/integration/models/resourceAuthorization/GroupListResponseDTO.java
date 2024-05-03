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
public class GroupListResponseDTO {
    List<Group> list;
}
