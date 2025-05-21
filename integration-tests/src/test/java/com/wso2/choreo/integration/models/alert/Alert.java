package com.wso2.choreo.integration.models.alert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Alert {

    private String orgId;
    private String envId;
    private String publisher;
    private String time;
    private String severity;
    private MetaData metaData;
    private Properties properties;
}
