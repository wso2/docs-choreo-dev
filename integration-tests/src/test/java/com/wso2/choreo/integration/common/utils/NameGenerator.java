package com.wso2.choreo.integration.common.utils;

import java.util.Date;
import java.util.UUID;

public class NameGenerator {

    public static String generateUniqueName(String baseName) {
        String timestamp = String.valueOf(new Date().getTime());
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return baseName.concat("_").concat(timestamp).concat("_").concat(uuid);
    }
}
