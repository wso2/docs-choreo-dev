package com.wso2.choreo.integration.models.platformServices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseServerPowerPayload {
    private String action;
}
