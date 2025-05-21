/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package com.wso2.choreo.integration.common;

import com.consol.citrus.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper functions for generating and formatting messages.
 */
public class MessageUtils {
    public static String generateStringFromTemplate(String templateRelativePath, Map<String, String> params)
            throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(templateRelativePath);
        Writer writer = new StringWriter();
        mustache.execute(writer, params).flush();
        return writer.toString();
    }

    public static String generateGQLPayload(String graphQuery) throws JsonProcessingException {
        Map<String, String> gqlRequestPayload = new HashMap<>() {
            {
                put("query", graphQuery);
            }
        };
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(gqlRequestPayload);
    }

    public static String generateJson(Map<String, Object> keyValues) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(keyValues);
    }

    public static void logCitrusResponse(Logger log, Message message) {
        log.debug("================================================================================================");
        log.debug("Received response payload << " + message.getPayload(String.class));
        log.debug("================================================================================================");
    }
}
