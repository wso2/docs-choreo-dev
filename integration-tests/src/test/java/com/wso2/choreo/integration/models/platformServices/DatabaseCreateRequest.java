package com.wso2.choreo.integration.models.platformServices;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseCreateRequest {
    @NotNull
    private String name;
}
