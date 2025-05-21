package com.wso2.choreo.integration.models.observability;


import lombok.Data;

@Data
public class ObservabilityLogs {
    private Columns[] columns;
    private String[][] rows;
}
