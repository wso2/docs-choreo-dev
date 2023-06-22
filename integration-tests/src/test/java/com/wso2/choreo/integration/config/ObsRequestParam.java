package com.wso2.choreo.integration.config;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ObsRequestParam {

    private String startTime;
    private String endTime;
    private String releaseId;
    private String namespace;
    private String interval;
    private String region;
    private String sort;
    private String limit;
    private String bin;
}
