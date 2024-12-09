package com.wso2.choreo.integration.models.platformServices;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Database {
    private String name;
    private boolean display_on_marketplace;
    private DatabaseStatus status;

}
