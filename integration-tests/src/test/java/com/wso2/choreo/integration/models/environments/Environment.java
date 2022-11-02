package com.wso2.choreo.integration.models.environments;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Environment {

    private String apiEnvName;
    private String dev;
    private String id;
    private String name;
}
