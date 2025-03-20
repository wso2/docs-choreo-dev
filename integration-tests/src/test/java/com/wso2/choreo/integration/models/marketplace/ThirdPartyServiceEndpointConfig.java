package com.wso2.choreo.integration.models.marketplace;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ThirdPartyServiceEndpointConfig {

    Map<String, Config> configs;
}
