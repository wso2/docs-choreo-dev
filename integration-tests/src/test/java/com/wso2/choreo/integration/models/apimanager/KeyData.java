package com.wso2.choreo.integration.models.apimanager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyData {


    private String apikey;
    private String invokeUrl;
    private long validityTime;

}
