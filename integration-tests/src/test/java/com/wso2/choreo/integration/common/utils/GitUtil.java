package com.wso2.choreo.integration.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;


public class GitUtil {


    public GitUtil() {
    }

    public static String getGitRepo(String repoName, boolean autoInit, boolean isPrivate, String gitignoreTemplate) throws JsonProcessingException {
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("name", repoName);
                put("auto_init", autoInit);
                put("private", isPrivate);
                put("gitignore_template", gitignoreTemplate);
            }
        };

        ObjectMapper objectMapper = new ObjectMapper();
        return ObjectMapperUtil.mapToString(requestBodyMap);
    }

    public static String mergePR() throws JsonProcessingException {
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("commit_title", "Merge initial PR");
            }
        };

        return ObjectMapperUtil.mapToString(requestBodyMap);
    }

    /**
     * Merge new code.
     *
     * @param commitMessage Commit message.
     * @param content       Encoded content of the file.
     * @param sha           SHA from GET <GitHub>/repos/<OWNER>/<REPO>/contents/<FILE>
     */
    public static String mergeNewCode(String commitMessage, String content, String sha) {
        return "{\n" +
                "    \"message\":"+"\"" + commitMessage +"\""+ " ,\n" +
                "    \"content\":"+"\"" + content+"\"" + ",\n" +
                "    \"sha\":"+"\"" + sha+"\"" + "\n}";
    }


}
