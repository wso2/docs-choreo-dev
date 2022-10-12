package com.wso2.choreo.integration.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.common.exceptions.RequestExecutionException;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GitUtil {

    private static final Logger LOGGER = Logger.getLogger(GitUtil.class.getName());
    private static final String GH_URL = Configuration.getConfig(ConfigDefinition.GITHUB_ENDPOINT);
    private static final String GH_ORG = Configuration.getConfig(ConfigDefinition.GITHUB_ORG);
    private static final String AUTH_HEADER = Constant.GITHUB_AUTH_HEADER_PREFIX.concat(Configuration.getConfig(ConfigDefinition.GITHUB_PAT));


    public GitUtil() {
    }

    public static Response initGitHubRepo(String repoName, boolean autoInit, boolean isPrivate, String gitignoreTemplate) throws IOException, RequestExecutionException {
        String requestURI = GH_URL + "/orgs/" + GH_ORG + "/repos";
        LOGGER.log(Level.INFO, requestURI);
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("name", repoName);
                put("auto_init", autoInit);
                put("private", isPrivate);
                put("gitignore_template", gitignoreTemplate);
            }
        };


        return HttpClientUtil.httpPOST(requestURI, ObjectMapperUtil.mapToString(requestBodyMap), AUTH_HEADER, "",0);
    }

    public static Response mergePR(String repoName, String prNumber) throws IOException, RequestExecutionException {
        String requestURI = GH_URL + "/repos/".concat(GH_ORG).concat("/").concat(repoName).concat("/pulls/" + prNumber + "/merge");
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("commit_title", "Merge initial PR");
            }
        };
        return HttpClientUtil.httpPUT(requestURI, ObjectMapperUtil.mapToString(requestBodyMap), AUTH_HEADER, "",0);
    }

    /**
     * Merge new code.
     *
     * @param repoName      GitHub repo name.
     * @param path          GitHub's resource path.
     * @param commitMessage Commit message
     * @param content       Encoded content
     */
    public static Response mergeNewCode(String repoName, String path, String commitMessage, String content) throws RequestExecutionException, IOException {

        String requestUrl = GH_URL + "/repos/" + GH_ORG + "/" + repoName + "/contents/" + path;
        Response response = HttpClientUtil.httpGET(requestUrl, AUTH_HEADER, "",0);
        JsonObject jsonObject = new JsonParser().parse(response.getRes()).getAsJsonObject();
        String serviceBalSha = jsonObject.get("sha").getAsString();
        String request =  "{\n" +
                "    \"message\":" + "\"" + commitMessage + "\"" + " ,\n" +
                "    \"content\":" + "\"" + content + "\"" + ",\n" +
                "    \"sha\":" + "\"" + serviceBalSha + "\"" + "\n}";
        return HttpClientUtil.httpPUT(requestUrl,request,AUTH_HEADER,"",0);
    }


}
