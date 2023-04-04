package com.wso2.choreo.integration.apis.github;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.models.github.Content;
import com.wso2.choreo.integration.models.response.Response;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.HashMap;

@Log4j2
public class GitHub extends ControlPlaneAPI {


    public GitHub() {
    }

    public static String getGitHubRepoUrl(String repoName) {

        return "https://github.com/" + GH_ORG + "/" + repoName;
    }

    public static Response initGitHubRepo(String repoName, boolean autoInit, boolean isPrivate, String gitignoreTemplate) throws IOException {
        String requestURI = GH_URL + "/orgs/" + GH_ORG + "/repos";
        log.info(requestURI);
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("name", repoName);
                put("auto_init", autoInit);
                put("private", isPrivate);
                put("gitignore_template", gitignoreTemplate);
            }
        };


        return HttpClientUtil.httpPOST(requestURI, ObjectMapperUtil.mapToString(requestBodyMap), AUTH_HEADER, "");
    }

    public static Response mergePR(String repoName, String prNumber) throws IOException {
        String requestURI = GH_URL + "/repos/".concat(GH_ORG).concat("/").concat(repoName).concat("/pulls/" + prNumber + "/merge");
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("commit_title", "Merge initial PR");
            }
        };
        return HttpClientUtil.httpPUT(requestURI, ObjectMapperUtil.mapToString(requestBodyMap), AUTH_HEADER, "");
    }


    public static void getSha(String repoName, String path) {
        String requestUrl = GH_URL + "/repos/" + GH_ORG + "/" + repoName + "/contents/" + path;
        Response response = HttpClientUtil.httpGET(requestUrl, AUTH_HEADER, "");
        JsonObject jsonObject = new JsonParser().parse(response.getRes()).getAsJsonObject();
        String serviceBalSha = jsonObject.get("sha").getAsString();
    }

    /**
     * Merge new code.
     *
     * @param repoName      GitHub repo name.
     * @param path          GitHub's resource path.
     * @param commitMessage Commit message
     * @param content       Encoded content
     */
    public static Response mergeNewCode(String repoName, String path, String commitMessage, String content) throws IOException {

        String requestUrl = GH_URL + "/repos/" + GH_ORG + "/" + repoName + "/contents/" + path;
        Response response = HttpClientUtil.httpGET(requestUrl, AUTH_HEADER, "");
        JsonObject jsonObject = new JsonParser().parse(response.getRes()).getAsJsonObject();
        String serviceBalSha = jsonObject.get("sha").getAsString();
        String request = "{\n" +
                "    \"message\":" + "\"" + commitMessage + "\"" + " ,\n" +
                "    \"content\":" + "\"" + content + "\"" + ",\n" +
                "    \"sha\":" + "\"" + serviceBalSha + "\"" + "\n}";
        return HttpClientUtil.httpPUT(requestUrl, request, AUTH_HEADER, "");
    }

    public static void createNewFile(String repoName, String path, String content) throws IOException {
        String requestUrl = GH_URL + "/repos/" + GH_ORG + "/" + repoName + "/contents/" + path;
        Content content1 = Content.builder().message("Create initial " + path).content(content).build();
        String payload = ObjectMapperUtil.mapObjectToString(content1);
        Response response = HttpClientUtil.httpPUT(requestUrl, payload, AUTH_HEADER, "");
    }

    public static Response deleteGitHubRepo(String repoName) {
        String requestURI = GH_URL + "/repos/" + GH_ORG + "/" + repoName;
        return HttpClientUtil.httpDELETE(requestURI, AUTH_HEADER, "");

    }

    public static Response createNewBranch(String orgName, String repoName, String branchName, String newBranchName)
            throws IOException {
        System.out.println("create new branch");
        String requestURI = GH_URL + "/repos/" + orgName + "/" + repoName + "/git/refs";
        System.out.println(requestURI);
        // getting the sha of the required branch
        String shaRequestURI = GH_URL + "/repos/" + orgName + "/" + repoName + "/git/refs/heads/" + branchName;
        System.out.println(shaRequestURI);
        Response response = HttpClientUtil.httpGET(shaRequestURI, AUTH_HEADER, "");
        JsonObject responseJsonObject = new JsonParser().parse(response.getRes()).getAsJsonObject();
        System.out.println(responseJsonObject);
        JsonObject shaJsonObject = responseJsonObject.get("object").getAsJsonObject();
        String sha = shaJsonObject.get("sha").getAsString();

        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("ref", "refs/heads/" + newBranchName);
                put("sha", sha);
            }
        };

        System.out.println(requestBodyMap);

        return HttpClientUtil.httpPOST(requestURI, ObjectMapperUtil.mapToString(requestBodyMap), AUTH_HEADER, "");
    }

    public static Response fetchUserReposFromGitHub() {
        String requestUrl = GH_URL + "/orgs/" + GH_ORG + "/repos";
        return HttpClientUtil.httpGET(requestUrl, AUTH_HEADER, "");
    }


    public static Response mergeInitialPR(String repoName, String message) throws IOException {
        String requestURI = GH_URL + "/repos/" + GH_ORG + "/" + repoName + "/pulls/1/merge";
        HashMap<String, Object> requestBodyMap = new HashMap<>() {
            {
                put("commit_title", message);
            }
        };
        String request = ObjectMapperUtil.mapToString(requestBodyMap);
        return HttpClientUtil.httpPUT(requestURI, request, AUTH_HEADER, "");
    }
    
    public static Response deleteBranch(String orgName, String repoName, String branchName) throws IOException {
        String requestURI = GH_URL + "/repos/" + orgName + "/" + repoName + "/git/refs/heads/" + branchName;
        return HttpClientUtil.httpDELETE(requestURI, AUTH_HEADER, "");
    }
}
