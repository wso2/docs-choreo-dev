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

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.exceptions.ValidationException;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.testng.spring.TestNGCitrusSpringSupport;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.JsonArray;
import com.wso2.choreo.integration.apis.apimanager.ApiManager;
import com.wso2.choreo.integration.apis.component.Component;
import com.wso2.choreo.integration.apis.configmgt.ConfigManagement;
import com.wso2.choreo.integration.models.graphql.CreateNewDeploymentTrackResponseDTO;
import com.wso2.choreo.integration.apis.devops.DevopsPortalApi;
import com.wso2.choreo.integration.apis.graphql.GraphQL;
import com.wso2.choreo.integration.apis.keysetmanagement.KeysetManagementService;
import com.wso2.choreo.integration.apis.observability.AuditLogsService;
import com.wso2.choreo.integration.apis.observability.DPObsApiService;
import com.wso2.choreo.integration.apis.proxydeployer.ProxyDeployer;
import com.wso2.choreo.integration.common.choreoproject.ApiVersion;
import com.wso2.choreo.integration.common.choreoproject.BalConfig;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.common.exceptions.ComponentRetrieveException;
import com.wso2.choreo.integration.common.exceptions.DeploymentStatusByVersionFailureException;
import com.wso2.choreo.integration.common.exceptions.InvokeAPICheckException;
import com.wso2.choreo.integration.common.exceptions.InvokeInformationNotFoundException;
import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import com.wso2.choreo.integration.common.exceptions.ProjectRetrievalException;
import com.wso2.choreo.integration.common.exceptions.TokenRetrievalException;
import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.NameGenerator;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.common.utils.SleepUtil;
import com.wso2.choreo.integration.config.ConfigDefinition;
import com.wso2.choreo.integration.config.Configuration;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.GraphqlDTO;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.code.Repository;
import com.wso2.choreo.integration.models.commithistory.Commit;
import com.wso2.choreo.integration.models.endpoints.Endpoint;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.environments.ProxyEnvironment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.graphql.CreateByocComponentResponseDTO;
import com.wso2.choreo.integration.models.graphql.CreateComponentResponseDTO;
import com.wso2.choreo.integration.models.invokeinfor.InvokeInformation;
import com.wso2.choreo.integration.models.keymanager.KeyGenResponseDTO;
import com.wso2.choreo.integration.models.observability.ObservabilityIdInformation;
import com.wso2.choreo.integration.models.proxyapi.Build;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import com.wso2.choreo.integration.models.proxyapi.ProxyDeployment;
import com.wso2.choreo.integration.models.proxyapi.TestSessionResponse;
import com.wso2.choreo.integration.models.response.Response;
import com.wso2.choreo.integration.models.revision.RevisionWrapper;
import com.wso2.choreo.integration.models.webhook.Trigger;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.testng.Assert;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;

@Log4j2
public class ComponentUtils {

    private static final String timestampRegexMatch = "^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2}Z|\\d{2}.\\d{2}Z|\\d{2}.\\d{3}Z|\\d{2}.\\d{4}Z|\\d{2}.\\d{5}Z|\\d{2}.\\d{6}Z|\\d{2}.\\d{7}Z)";
    private static final String APIS_ENDPOINT = Constant.APIS_ENDPOINT;

    private static final int MAX_DEPLOY_RETRY_COUNT = 5;

    public static ChoreoComponent getReusableComponent(TestNGCitrusSpringSupport runner, String accessToken,
            Repository repo,
            String testName, Map<Endpoints, HttpClient> citrusClients,
            ComponentFlavour componentFlavour, String componentType, String... branchName) throws Exception {

        ChoreoOrganization org = TestContext.getTestOrg();
        String projectName = "integration-test-project-V2";

        Optional<ChoreoProject> existingProject = org.getProjectByName(accessToken, projectName);
        ChoreoProject project;
        if (existingProject.isEmpty()) {
            HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
            String projectHandler = NameGenerator.generateThreadUniqueName();
            project = GraphQL.createProject(runner, appServiceClient, Constant.region.US.toString(), accessToken,
                    projectName, projectHandler);
        } else {
            project = existingProject.get();
        }

        String componentName = testName + "component";
        Optional<ChoreoComponent> component = ComponentUtils.getComponentByName(runner, accessToken, citrusClients,
                project, componentName);
        ChoreoComponent serviceComponent = new ChoreoComponent();

        if (component.isEmpty()) {
            if (componentType.equals(Constant.displayType.ballerinaService.name())) {
                GraphqlDTO dto = ComponentUtils.createBallerinaServiceComponentRequest(componentName, project, repo);
                serviceComponent = createComponent(runner, citrusClients, accessToken, dto, componentFlavour);
            } else if (componentType.equals(Constant.displayType.byocService.name())) {
                GraphqlDTO dto = ComponentUtils.createByocComponentRequest(componentName, project, repo);
                serviceComponent = createComponent(runner, citrusClients, accessToken, dto, componentFlavour,
                        branchName);
            }
        } else {
            serviceComponent = component.get();
        }

        serviceComponent.setOrganization(org);

        return serviceComponent;
    }

    public static Optional<ChoreoComponent> getComponentByName(TestActionRunner runner, String accessToken,
            Map<Endpoints, HttpClient> citrusClients, ChoreoProject project, String componentName)
            throws ComponentRetrieveException {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        try {
            List<ChoreoComponent> components = GraphQL.getProjectComponents(runner, cpProjectsClient, project.getId(),
                    accessToken);

            for (int i = 0; i < components.size(); ++i) {
                String name = components.get(i).getName();

                if (componentName.equals(name)) {
                    String componentHandler = components.get(i).getHandler();
                    return Optional.of(GraphQL.getComponentDetails(runner, cpProjectsClient, project.getId(),
                            componentHandler, accessToken));
                }
            }
        } catch (IOException e) {
            throw new ComponentRetrieveException("Component named: " + componentName + "does not exist in " +
                    project.getName() + " project");
        }

        return Optional.empty();
    }

    public static ChoreoProject getProjectByName(String projectName, String accessToken)
            throws ProjectRetrievalException {
        ChoreoOrganization org = TestContext.getTestOrg();
        Optional<ChoreoProject> existingProject = org.getProjectByName(accessToken, projectName);

        if (existingProject.isPresent()) {
            return existingProject.get();
        } else {
            throw new RuntimeException("Project named: " + projectName + "does not exist");
        }
    }

    private static ProxyAPI createApiProxy(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken, String apiName) throws Exception {
        HttpClient stsClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        ProxyAPI proxyAPI = ApiManager.createApiProxy(runner, stsClient, accessToken, apiName);
        return proxyAPI;
    }

    public static GraphqlDTO createRestApiComponentRequest(String name, ChoreoProject project, Repository repo) {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));

        return GraphqlDTO.builder().name(name).triggerID("null").srcGitRepoUrl(repo.getRepoUrl())
                .projectId(project.getId()).orgId(orgId).orgHandler(orgHandle)
                .repositoryType(Constant.NON_EMPTY_REPO_TYPE).repositoryBranch(repo.getBranch())
                .repositorySubPath(repo.getSubPath()).displayType(Constant.displayType.restAPI.name()).build();
    }

    public static GraphqlDTO createBYOIComponentRequest(String name, String projectId, String imageUrl, String registryId) {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));

        return  GraphqlDTO.builder()
                            .name(name.toLowerCase())
                            .orgId(orgId)
                            .orgHandler(orgHandle)
                            .displayName(name)
                            .componentType(Constant.displayType.byoiService.name())
                            .projectId(projectId)
                            .imageUrl(imageUrl)
                            .registryId(registryId)
                            .build();
    }

    public static GraphqlDTO createManualTriggerComponentRequest(String name, ChoreoProject project, Repository repo) {
        GraphqlDTO graphqlDTO = createBallerinaServiceComponentRequest(name, project, repo);
        graphqlDTO.setDisplayType(Constant.displayType.manualTrigger.name());
        return graphqlDTO;
    }

    public static GraphqlDTO createBallerinaServiceComponentRequest(String name, ChoreoProject project,
            Repository repo) {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));

        return GraphqlDTO.builder().name(name).triggerID("null").srcGitRepoUrl(repo.getRepoUrl())
                .projectId(project.getId()).orgId(orgId).orgHandler(orgHandle)
                .repositoryType(Constant.NON_EMPTY_REPO_TYPE).repositoryBranch(repo.getBranch())
                .repositorySubPath(repo.getSubPath()).displayType(Constant.displayType.ballerinaService.name()).build();
    }

    public static GraphqlDTO createByocComponentRequest(String name, ChoreoProject project, Repository repo) {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));

        return GraphqlDTO.builder().name(name).srcGitRepoUrl(repo.getRepoUrl()).projectId(project.getId()).orgId(orgId)
                .displayType(Constant.displayType.restAPI.name()).orgHandler(orgHandle)
                .oasFilePath(repo.getOasFilePath()).dockerContext(repo.getDockerContext())
                .dockerfilePath(repo.getDockerfilePath()).build();
    }

    public static GraphqlDTO createPrismMockComponentRequest(String name, ChoreoProject project, Repository repo) {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));

        return GraphqlDTO.builder()
                .name(name)
                .displayType(Constant.displayType.prismMockService.name())
                .orgId(orgId)
                .orgHandler(orgHandle)
                .projectId(project.getId())
                .componentType(Constant.displayType.prismMockService.name())
                .buildContext(repo.getBuildContext())
                .srcGitRepoUrl(repo.getRepoUrl())
                .srcGitRepoBranch(repo.getBranch())
                .buildpackId(Buildpack.PRISM_MOCK.getId())
                .build();
    }

    public static GraphqlDTO createWebappComponentRequest(String name, ChoreoProject project, 
            GraphqlDTO.ByocWebAppsConfig webAppsConfig) {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        return GraphqlDTO.builder().name(name).orgId(orgId).orgHandler(orgHandle).projectId(project.getId())
                .byocWebAppsConfig(webAppsConfig).build();
    }

    public static GraphqlDTO createBuildpackComponentRequest(String name, ChoreoProject project, Repository repo) {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        Buildpack buildpack = Buildpack.GOLANG;

        return GraphqlDTO.builder().name(name).srcGitRepoUrl(repo.getRepoUrl()).projectId(project.getId()).orgId(orgId)
                .orgHandler(orgHandle).buildpackId(buildpack.getId()).languageVersion(buildpack.getVersion())
                .buildContext(repo.getBuildContext()).build();
    }

    public static GraphqlDTO createGrpahQLComponentRequest(String name, ChoreoProject project, Repository repo) {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));

        return GraphqlDTO.builder().name(name).triggerID("null").srcGitRepoUrl(repo.getRepoUrl())
                .projectId(project.getId()).orgId(orgId).orgHandler(orgHandle)
                .repositoryType(Constant.NON_EMPTY_REPO_TYPE).repositoryBranch(repo.getBranch())
                .repositorySubPath(repo.getSubPath()).displayType(Constant.displayType.graphql.name()).build();
    }

    public static GraphqlDTO createWebhookComponentRequest(String name, ChoreoProject project, Repository repo,
            Trigger trigger) {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));

        return GraphqlDTO.builder().name(name).triggerID(trigger.getId()).triggerChannels(trigger.getChannels())
                .srcGitRepoUrl(repo.getRepoUrl()).projectId(project.getId()).orgId(orgId).orgHandler(orgHandle)
                .repositoryType(Constant.NON_EMPTY_REPO_TYPE).repositoryBranch(repo.getBranch())
                .repositorySubPath(repo.getSubPath()).displayType(Constant.displayType.webhook.name()).build();
    }

    public static ChoreoProject createProject(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients,
            String accessToken, String region) throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String projectName = NameGenerator.generateUniqueName(Constant.TEST_PROJECT_NAME_PREFIX);
        String projectHandler = NameGenerator.generateThreadUniqueName();
        ChoreoProject project = GraphQL.createProject(runner, appServiceClient, region, accessToken, projectName,
                projectHandler);
        Assert.assertNotNull(project.getId(), "Project ID is not null.");
        return project;
    }

    public static ChoreoComponent createComponent(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients,
            String accessToken, GraphqlDTO dto,
            ComponentFlavour componentFlavour, String... branchName) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        GraphqlDTO graphqlDTO;

        if (componentFlavour.equals(ComponentFlavour.BYOC)) {
            dto.setComponentType("byocService");
            Optional<CreateByocComponentResponseDTO> responseDTO = GraphQL.createBYOCComponent(runner, appServiceClient,
                    dto, accessToken, branchName);

            graphqlDTO = GraphqlDTO.builder().projectId(responseDTO.get().getProjectId())
                    .componentHandler(responseDTO.get().getHandle()).build();
        } else if (componentFlavour.equals(ComponentFlavour.BUILDPACK)) {
            dto.setComponentType("buildpackService");
            Optional<CreateByocComponentResponseDTO> responseDTO = GraphQL.createBuildpackComponent(runner,
                    appServiceClient,
                    dto, accessToken);
            String projectId = responseDTO.get().getProjectId();
            graphqlDTO = GraphqlDTO.builder().projectId(projectId)
                    .componentHandler(responseDTO.get().getHandle()).build();
            List<ChoreoComponent> components = GraphQL.getProjectComponents(runner, appServiceClient, projectId, accessToken);
            String componentId = components.get(0).getId();
            log.debug("Component Id: " + componentId);
            Component.waitForAsyncComponentCreationSuccess(runner, appServiceClient, accessToken, componentId);
        } else if (componentFlavour.equals(ComponentFlavour.PRISM_MOCK_SERVICE)) {
            Optional<CreateByocComponentResponseDTO> responseDTO = GraphQL.createPrismMockComponent(runner,
                    appServiceClient,
                    dto, accessToken);
            String projectId = responseDTO.get().getProjectId();
            graphqlDTO = GraphqlDTO.builder().projectId(projectId)
                    .componentHandler(responseDTO.get().getHandle()).build();
            List<ChoreoComponent> components = GraphQL.getProjectComponents(runner, appServiceClient, projectId, accessToken);
            String componentId = components.get(0).getId();
            log.debug("Component Id: " + componentId);
            Component.waitForAsyncComponentCreationSuccess(runner, appServiceClient, accessToken, componentId);
        } else if (componentFlavour.equals(ComponentFlavour.WEBAPP)) {
            dto.setComponentType("byocWebAppsDockerfileLess");
            Optional<CreateByocComponentResponseDTO> responseDTO = GraphQL.createWebappComponent(runner,
                    appServiceClient,
                    dto, accessToken);
            graphqlDTO = GraphqlDTO.builder().projectId(responseDTO.get().getProjectId())
                    .componentHandler(responseDTO.get().getHandle()).build();
        }

        else {
            String queryString = ObjectMapperUtil.mapObjectToString(
                    "templates/graphql/requests/createUserManagedComponent.mustache", dto);

            Optional<CreateComponentResponseDTO> responseDTO = GraphQL.createUserManagedComponent(runner,
                    appServiceClient, queryString, dto.getProjectId(), accessToken);

            Component.waitForComponentCreationSuccess(runner, appServiceClient, accessToken,
                    responseDTO.get().getProjectId(),
                    responseDTO.get().getId());

            graphqlDTO = GraphqlDTO.builder().projectId(responseDTO.get().getProjectId())
                    .componentHandler(responseDTO.get().getHandler()).build();
        }

        return GraphQL.retrieveComponent(runner, appServiceClient, accessToken,
                graphqlDTO);
    }

    public static Pair<ChoreoComponent, ProxyAPI> createProxyComponent(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, String componentName, String proxyAPIName,
            ChoreoProject project) throws Exception {
        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        int orgId = Integer.parseInt(Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID));
        ProxyAPI proxyAPI = ComponentUtils.createApiProxy(runner, citrusClients, accessToken, proxyAPIName);
        Assert.assertNotNull(proxyAPI.getId(), "Proxy API id not found");
        GraphqlDTO dto = GraphqlDTO.builder().name(componentName).displayName(componentName).triggerID("null")
                .projectId(project.getId()).orgId(orgId).orgHandler(orgHandle)
                .displayType(Constant.displayType.proxy.name()).apiId(proxyAPI.getId().replaceAll("\"", "")).build();
        String queryString = ObjectMapperUtil.mapObjectToString(
                "templates/api-proxy/graphqlQueryForComponentCreation.mustache", dto);
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Optional<CreateComponentResponseDTO> responseDTO = GraphQL.createUserManagedComponent(runner,
                cpProjectsClient, queryString, project.getId(), accessToken);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().projectId(responseDTO.get().getProjectId())
                .componentHandler(responseDTO.get().getHandler()).build();
        ChoreoComponent component = GraphQL.retrieveComponent(runner, cpProjectsClient, accessToken,
                graphqlDTO);
        return Pair.of(component, proxyAPI);
    }

    public static List<Environment> getDeploymentEnvironments(TestActionRunner runner,
            Map<Endpoints, HttpClient> citrusClients,
            String accessToken, ChoreoComponent component)
            throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        ChoreoOrganization org = component.getOrganization();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().orgUuid(org.getOrgUUID()).projectId(component.getProjectId())
                .build();

        return GraphQL.getDeploymentEndvironments(runner, cpProjectsClient, accessToken, graphqlDTO);
    }

    public static ComponentDeploymentStatusDTO deployComponent(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            List<Environment> environments, ComponentFlavour componentFlavour,
            BalConfig... balconfigs) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, appServiceClient, component.getId(), accessToken,
                component.getRepository().getBranchApp());

        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        String shaDate = latestCommit.getAuthor().getDate();
        String sha = latestCommit.getSha();

        String componentId = component.getId();
        ApiVersion apiVersion = component.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();

        String devEnvIdToDeploy = environments.get(0).getId();
        String branch = component.getRepository().getBranchApp();

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).latestVersionId(latestVersionId)
                .devEnvIdToDeploy(devEnvIdToDeploy).branch(branch).sha(sha).shaDate(shaDate).build();

        ComponentDeploymentStatusDTO deploymentStatusDTO = null;
        for (int i = 0; i < MAX_DEPLOY_RETRY_COUNT; ++i) {
            // Build component
            GraphQL.deployComponent(runner, appServiceClient, accessToken, graphqlDTO);
            try {
                validateBuild(runner, citrusClients, accessToken, component, latestCommit, environments);
                break;
            } catch (Exception e) {
                if (e.getCause() instanceof DeploymentStatusByVersionFailureException) {
                    log.error("Build failure detected, attempt number " + (i + 1), e);
                } else {
                    throw e;
                }
            }
        }

        if (component.getDisplayType().equals(Constant.displayType.ballerinaService.name())) {
            Map<String, String> argMap = new HashMap<>();
            argMap.put("componentId", component.getId());
            argMap.put("versionId", component.getLatestApiVersion().getId());
            argMap.put("releaseId", component.getReleaseIdByEnvironmentId(environments.get(0).getId()));
            argMap.put("commitHash", component.getLatestCommitHash(component.getCommitHistory(accessToken)));
            GraphQL.generateEndpoints(runner, appServiceClient, accessToken, argMap);

            argMap = new HashMap<>();
            argMap.put("componentId", component.getId());
            argMap.put("versionId", component.getLatestApiVersion().getId());
            argMap.put("releaseId", component.getReleaseIdByEnvironmentId(environments.get(0).getId()));
            List<Endpoint> endpoints = GraphQL.getEndpoints(runner, appServiceClient, accessToken, argMap);
            Endpoint endpoint = endpoints.get(0);
            Assert.assertEquals(endpoints.size(), 1);

            argMap = new HashMap<>();
            argMap.put("componentId", component.getId());
            argMap.put("versionId", component.getLatestApiVersion().getId());
            argMap.put("releaseId", component.getReleaseIdByEnvironmentId(environments.get(0).getId()));
            argMap.put("endpointId", endpoint.getId());
            argMap.put("displayName", endpoint.getDisplayName());
            argMap.put("apiContext", endpoint.getApiContext());
            argMap.put("apiDefinitionPath", endpoint.getApiDefinitionPath());
            argMap.put("visibility", Constant.EndpointVisibility.PUBLIC.value);
            Endpoint updatedEndpoint = GraphQL.updateEndpoint(runner, appServiceClient, accessToken, argMap);
            endpoints.set(0, updatedEndpoint);
        }

        if (componentFlavour.equals(ComponentFlavour.STANDARD)) {
            ConfigManagement.addConfiguration(runner, appServiceClient, component, commitHistory, environments.get(0),
                    balconfigs);
        }

        for (int i = 0; i < MAX_DEPLOY_RETRY_COUNT; ++i) {
            // Build component
            deploymentStatusDTO = deployBuiltComponent(runner, citrusClients, accessToken, component, latestCommit,
                    environments);
            try {
                validateComponentDeployment(runner, citrusClients, accessToken, component, latestCommit, environments);
                break;
            } catch (Exception e) {
                if (e.getCause() instanceof DeploymentStatusByVersionFailureException) {
                    log.error("DeployStatusByVersion failure detected, attempt number " + (i + 1), e);
                } else {
                    throw e;
                }
            }
        }
        if (deploymentStatusDTO == null) {
            throw new Exception("Component deployment failed");
        }

        return deploymentStatusDTO;
    }

    public static ComponentDeploymentStatusDTO deployAndValidateBuiltComponent(TestNGCitrusSpringSupport runner,
                                                               Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent testComponent,
                                                               List<Environment> environments) throws Exception {
        HttpClient choreoProjectsTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, choreoProjectsTestClient, testComponent.getId(), accessToken,
                testComponent.getRepository().getBranchApp());

        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        ComponentDeploymentStatusDTO componentDeploymentStatusDTO = ComponentUtils.deployBuiltComponent(runner, citrusClients, accessToken, testComponent, latestCommit,
                environments);
        try {
            ComponentUtils.validateComponentDeployment(runner, citrusClients, accessToken, testComponent, latestCommit, environments);
        } catch (Exception e) {
            if (e.getCause() instanceof DeploymentStatusByVersionFailureException) {
                log.error("DeployStatusByVersion failure detected", e);
            } else {
                throw e;
            }
        }

        return componentDeploymentStatusDTO;
    }

    public static ComponentDeploymentStatusDTO deployAndValidateBuiltComponentWithFlavour(TestNGCitrusSpringSupport runner,
                                                                               Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent testComponent,
                                                                               List<Environment> environments, ComponentFlavour componentFlavour,
                                                                                          BalConfig... balconfigs) throws Exception {
        HttpClient choreoProjectsTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, choreoProjectsTestClient, testComponent.getId(), accessToken,
                testComponent.getRepository().getBranchApp());

        if (componentFlavour.equals(ComponentFlavour.STANDARD)) {
            ConfigManagement.addConfiguration(runner, choreoProjectsTestClient, testComponent, commitHistory, environments.get(0),
                    balconfigs);
        }
        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        ComponentDeploymentStatusDTO componentDeploymentStatusDTO = ComponentUtils.deployBuiltComponent(runner, citrusClients, accessToken, testComponent, latestCommit,
                environments);
        try {
            ComponentUtils.validateComponentDeployment(runner, citrusClients, accessToken, testComponent, latestCommit, environments);
        } catch (Exception e) {
            if (e.getCause() instanceof DeploymentStatusByVersionFailureException) {
                log.error("DeployStatusByVersion failure detected", e);
            } else {
                throw e;
            }
        }

        return componentDeploymentStatusDTO;
    }


    public static ComponentDeploymentStatusDTO validateComponentDeployment(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken,
            ChoreoComponent component, Commit latestCommit,
            List<Environment> environments, Boolean... checkOnlyStatus) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        String componentId = component.getId();
        ApiVersion apiVersion = component.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();
        String devEnvIdToDeploy = environments.get(0).getId();

        ChoreoOrganization org = component.getOrganization();
        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", devEnvIdToDeploy);
        responseParams.put("sha", latestCommit.getSha());
        responseParams.put("versionId", latestVersionId);

        if (checkOnlyStatus.length > 0) {
            GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle())
                    .orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(devEnvIdToDeploy).build();
            if (checkOnlyStatus[0]) {
                return GraphQL.getDeploymentStatus(runner, appServiceClient, accessToken, graphqlDTO, responseParams);
            }
        }

        GraphqlDTO graphqlDTO = createDeploymentRequest(component, latestCommit, environments);
        GraphQL.getDeploymentStatusByVersion(runner, appServiceClient, accessToken, graphqlDTO);

        graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle())
                .orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(devEnvIdToDeploy).build();

        return GraphQL.getComponentDeploymentStatus(runner, appServiceClient, accessToken, graphqlDTO, responseParams);
    }

    public static void validateBuild(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken,
            ChoreoComponent component, Commit latestCommit,
            List<Environment> environments) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        GraphqlDTO graphqlDTO = createDeploymentRequest(component, latestCommit, environments);

        GraphQL.getDeploymentStatusByVersion(runner, appServiceClient, accessToken, graphqlDTO);
    }

    public static ComponentDeploymentStatusDTO deployBuiltComponent(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken,
            ChoreoComponent component, Commit latestCommit,
            List<Environment> environments) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        GraphqlDTO graphqlDTO = createDeploymentRequest(component, latestCommit, environments);

        String componentId = component.getId();
        ApiVersion apiVersion = component.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();
        String devEnvIdToDeploy = environments.get(0).getId();

        ChoreoOrganization org = component.getOrganization();
        graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle())
                .orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(devEnvIdToDeploy).build();

        GraphqlDTO imageDTO = GraphqlDTO.builder().componentId(componentId).versionId(latestVersionId).build();
        JsonArray images = GraphQL.getImageListWithRetry(runner, appServiceClient, accessToken, imageDTO, 30);

        if (images.isEmpty()) {
            throw new RuntimeException("Images not found for version ID : " + latestVersionId +
                    " in component ID : " + componentId);
        }

        GraphqlDTO graphqlDeployDTO = GraphqlDTO.builder().componentId(componentId).versionId(latestVersionId)
                .imageId(images.get(0).getAsJsonObject().get("imageId").getAsString()).environmentId(devEnvIdToDeploy)
                .build();
        GraphQL.deployBuiltComponent(runner, appServiceClient, accessToken, graphqlDeployDTO);

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", devEnvIdToDeploy);
        responseParams.put("sha", latestCommit.getSha());
        responseParams.put("versionId", latestVersionId);

        return GraphQL.getComponentDeploymentStatus(runner, appServiceClient, accessToken, graphqlDTO, responseParams);
    }

    private static GraphqlDTO createDeploymentRequest(ChoreoComponent component, Commit latestCommit,
            List<Environment> environments) throws Exception {
        String shaDate = latestCommit.getAuthor().getDate();
        String sha = latestCommit.getSha();

        String componentId = component.getId();
        ApiVersion apiVersion = component.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();

        String devEnvIdToDeploy = environments.get(0).getId();
        String branch = component.getRepository().getBranch();

        return GraphqlDTO.builder().componentId(componentId).latestVersionId(latestVersionId)
                .devEnvIdToDeploy(devEnvIdToDeploy).branch(branch).sha(sha).shaDate(shaDate).build();
    }

    public static Commit getLatestCommit(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken,
            ChoreoComponent component) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, appServiceClient, component.getId(), accessToken);

        return Commit.getLatestCommit(commitHistory);
    }

    public static ProxyAPIBuild deployProxyComponent(TestActionRunner runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            List<Environment> environments) throws Exception {
        HttpClient choreoEPClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);

        for (Environment env : environments) {
            ProxyDeployer.initiateDeployment(runner, choreoEPClient, accessToken, component.getId(),
                    component.getLatestApiVersion().getId(), env.getId());
        }

        ProxyAPIBuild apiBuilds = ProxyDeployer.getApiBuilds(runner, choreoEPClient, accessToken, component.getId(),
                component.getLatestApiVersion().getId());

        Build build = apiBuilds.getBuilds()[0];

        for (Environment env : environments) {
            ProxyDeployer.deployProxyAPI(runner, choreoEPClient, accessToken, component.getId(),
                    component.getLatestApiVersion().getId(), build.getBuildId(), env.getId());
        }

        return apiBuilds;
    }

    public static void promoteProxyComponent(TestActionRunner runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            List<Environment> environments, ProxyAPIBuild apiBuilds) throws Exception {
        HttpClient choreoEPClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);

        int srcEnvIndex = 0;
        int destEnvIndex = 1;

        Build build = apiBuilds.getBuilds()[0];

        while (destEnvIndex < environments.size()) {
            Environment srcEnv = environments.get(srcEnvIndex);
            Environment destEnv = environments.get(destEnvIndex);

            ProxyDeployer.promoteProxyAPI(runner, choreoEPClient, accessToken, component.getId(),
                    component.getLatestApiVersion().getId(), srcEnv.getId(), destEnv.getId(), build.getBuildId());

            ++srcEnvIndex;
            ++destEnvIndex;
        }
    }

    public static List<ProxyDeployment> getProxyDeployments(TestActionRunner runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            List<Environment> environments) throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String orgHandle = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_HANDLE);
        String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

        List<ProxyDeployment> proxyDeployments = new ArrayList<>();
        for (Environment env : environments) {
            GraphqlDTO dto = GraphqlDTO.builder()
                    .orgHandler(orgHandle).orgUuid(orgUuid).componentId(component.getId())
                    .versionId(component.getLatestApiVersion().getId()).environmentId(env.getId()).build();

            proxyDeployments.add(GraphQL.getProxyComponentDeployment(runner, cpProjectsClient, accessToken, dto));
        }

        return proxyDeployments;
    }

    public static ComponentDeploymentStatusDTO getComponentDeploymentStatus(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component, String env)
            throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String envId = component.getLatestAppEnvId(env);
        String componentId = component.getId();
        ApiVersion apiVersion = component.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();

        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, cpProjectsClient, component.getId(), accessToken);

        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        String sha = latestCommit.getSha();

        ChoreoOrganization org = component.getOrganization();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle())
                .orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(envId).build();

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", envId);
        responseParams.put("sha", sha);
        responseParams.put("versionId", latestVersionId);

        return GraphQL.getComponentDeploymentStatus(runner, cpProjectsClient, accessToken, graphqlDTO, responseParams);
    }

    public static List<ComponentDeploymentStatusDTO> promoteComponent(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            List<Environment> environments, ComponentFlavour componentFlavour,
            BalConfig... balconfigs) throws Exception {
        List<ComponentDeploymentStatusDTO> promotionStatus = null;
        promotionStatus = promote(runner, citrusClients, accessToken, component, environments, componentFlavour,
                balconfigs);
        return promotionStatus;
    }

    public static List<ComponentDeploymentStatusDTO> promoteComponent(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            List<Environment> environments, ComponentFlavour componentFlavour, ChoreoProject project,
            BalConfig... balconfigs) throws Exception {

        HttpClient apimClient = citrusClients.get(Endpoints.STS_ENDPOINT);
        String query = "context:/" + Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID) + "/"
                + project.getHandler() + "/" + component.getName() + "/v1.0";
        ApiManager.searchAPIByQuery(runner, apimClient, accessToken, query);

        List<ComponentDeploymentStatusDTO> promotionStatus = null;
        promotionStatus = promote(runner, citrusClients, accessToken, component, environments, componentFlavour,
                balconfigs);
        return promotionStatus;
    }

    private static List<ComponentDeploymentStatusDTO> promote(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            List<Environment> environments, ComponentFlavour componentFlavour, BalConfig... balconfigs)
            throws Exception {

        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, appServiceClient, component.getId(),
                accessToken, component.getRepository().getBranchApp());
        List<ComponentDeploymentStatusDTO> deploymentStatus = new ArrayList<>();
        int srcEnvIndex = 0;
        int destEnvIndex = 1;

        String displayType = component.getDisplayType();

        while (destEnvIndex < environments.size()) {
            Environment srcEnv = environments.get(srcEnvIndex);
            Environment destEnv = environments.get(destEnvIndex);

            for (int i = 0; i < 5; i++) { // Retry up to 5 times if Endpoint is not active after promotion

                if (displayType.equals(Constant.displayType.ballerinaService.name())
                        || displayType.equals(Constant.displayType.byocService.name())
                        || displayType.equals(Constant.displayType.buildpackService.name())
                        || displayType.equals(Constant.AppType.MI_API_SERVICE.value)
                        || displayType.equals(Constant.displayType.prismMockService.name())) {
                    Map<String, String> argMap = new HashMap<>();
                    argMap.put("componentId", component.getId());
                    argMap.put("versionId", component.getLatestApiVersion().getId());
                    argMap.put("sourceReleaseId", component.getReleaseIdByEnvironmentId(srcEnv.getId()));
                    argMap.put("targetEnvironmentId", destEnv.getId());
                    validateEndpointExistence(runner, citrusClients, accessToken, component, srcEnv);
                    GraphQL.promoteEndpoints(runner, appServiceClient, accessToken, argMap);
                }

                if (componentFlavour.equals(ComponentFlavour.STANDARD)) {
                    ConfigManagement.addConfiguration(runner, appServiceClient, component, commitHistory, destEnv,
                            balconfigs);
                }

                String componentId = component.getId();
                ApiVersion apiVersion = component.getLatestApiVersion();
                String latestVersionId = apiVersion.getId();
                String sourceReleaseId = component.getReleaseIdForEnvironment(srcEnv);
                String latestAppEnvId = destEnv.getId();

                GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).apiVersionId(latestVersionId)
                        .sourceReleaseId(sourceReleaseId).targetEnvironmentId(latestAppEnvId).build();
                GraphQL.promoteComponent(runner, appServiceClient, accessToken, graphqlDTO);
                SleepUtil.sleep(5);
                ComponentDeploymentStatusDTO statusDTO = getComponentPromotionStatus(runner, componentId, latestVersionId,
                        latestAppEnvId, commitHistory, appServiceClient, accessToken, component);
                deploymentStatus.add(statusDTO);

                if (displayType.equals(Constant.displayType.ballerinaService.name())
                        || displayType.equals(Constant.AppType.MI_API_SERVICE.value)
                        || displayType.equals(Constant.displayType.byocService.name())) {
                    if (isPromotionEndpointStatusActive(runner, citrusClients, accessToken, component,
                            statusDTO, srcEnv)) {
                        break;
                    }
                } else {
                    break;
                }
            }

            ++srcEnvIndex;
            ++destEnvIndex;
        }
        return deploymentStatus;
    }

    private static ComponentDeploymentStatusDTO getComponentPromotionStatus(TestNGCitrusSpringSupport runner,
            String componentId, String latestVersionId, String latestAppEnvId, List<Commit> commitHistory,
            HttpClient appServiceClient, String accessToken, ChoreoComponent component)
            throws IOException, NoLatestCommitHashFoundException {
        ChoreoOrganization org = component.getOrganization();
        GraphqlDTO graphqlDT = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle())
                .orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(latestAppEnvId).build();
        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        String sha = latestCommit.getSha();

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", latestAppEnvId);
        responseParams.put("sha", sha);
        responseParams.put("versionId", latestVersionId);

        return GraphQL.getComponentDeploymentStatus(runner, appServiceClient, accessToken, graphqlDT, responseParams);
    }

    private static boolean isPromotionEndpointStatusActive(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            ComponentDeploymentStatusDTO statusDTO, Environment srcEnv) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        List<Endpoint> endpoints = getEndpoints(runner, citrusClients, accessToken, component, statusDTO);

        boolean isEndpointsActive = true;
        for (Endpoint endpoint : endpoints) {
            if (!endpoint.getState().equals("Active")) {
                log.error("Endpoint status on promotion was expected to be Active, but was " + endpoint.getState());
                HashMap<String, String> argMap = new HashMap<>();
                argMap.put("componentId", component.getId());
                argMap.put("versionId", component.getLatestApiVersion().getId());
                argMap.put("releaseId", component.getReleaseIdByEnvironmentId(srcEnv.getId()));
                argMap.put("endpointId", endpoint.getId());
                argMap.put("displayName", endpoint.getDisplayName());
                argMap.put("apiContext", endpoint.getApiContext());
                argMap.put("apiDefinitionPath", endpoint.getApiDefinitionPath());
                argMap.put("visibility", Constant.EndpointVisibility.PUBLIC.value);
                GraphQL.updateEndpoint(runner, appServiceClient, accessToken, argMap);

                isEndpointsActive = false;
            }
        }

        return isEndpointsActive;
    }

    public static ComponentDeploymentStatusDTO getComponenetPromotionStatus(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component) throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String latestAppEnvId = component.getLatestAppEnvId(Constant.PROD_ENVIRONMENT);

        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, cpProjectsClient, component.getId(), accessToken);
        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        String sha = latestCommit.getSha();
        String componentId = component.getId();
        ApiVersion apiVersion = component.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();

        ChoreoOrganization org = component.getOrganization();
        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle())
                .orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(latestAppEnvId).build();

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", latestAppEnvId);
        responseParams.put("sha", sha);
        responseParams.put("versionId", latestVersionId);

        return GraphQL.getComponentDeploymentStatus(runner, cpProjectsClient, accessToken, graphqlDTO, responseParams);
    }

    public static void invokeApiEndpoint(String accessToken, ChoreoComponent component, Constant.Environment env)
            throws Exception {
        InvokeInformation invokeInformation = component.getInvokeInformation(accessToken,
                Constant.displayType.restAPI.name(), env.name());
        String requestURI = invokeInformation.getInvokeUrl();
        if (requestURI == null) {
            throw new InvokeInformationNotFoundException();
        }
        requestURI = requestURI.concat("/")
                .concat("greeting")
                .concat("?name=testUser");
        // Escaping the quotations
        String apiKey = component.getAPIKeyForInvoke(accessToken, invokeInformation.getApiId(),
                env.name()).replace("\"", "");
        HttpGet request = new HttpGet(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity());

            if (statusCode != HttpStatus.OK.value()) {
                throw new InvokeAPICheckException(statusCode, responseBody);
            }
        }
    }

    public static void invokeApiEndpointMultipleTimes(String accessToken, ChoreoComponent component,
            Constant.Environment env, int attempts) throws Exception {
        InvokeInformation invokeInformation = component.getInvokeInformation(accessToken,
                Constant.displayType.restAPI.name(), env.name());
        String requestURI = invokeInformation.getInvokeUrl();
        if (requestURI == null) {
            throw new InvokeInformationNotFoundException();
        }
        requestURI = requestURI.concat("/")
                .concat("greeting")
                .concat("?name=testUser");
        // Escaping the quotations
        String apiKey = component.getAPIKeyForInvoke(accessToken, invokeInformation.getApiId(),
                env.name()).replace("\"", "");
        HttpGet request = new HttpGet(requestURI);
        request.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
        request.setHeader(HttpHeaders.CONTENT_TYPE, Constant.APPLICATION_JSON);
        request.setHeader("API-Key", apiKey);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            for (int i = 0; i < attempts; i++) {
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    int statusCode = response.getStatusLine().getStatusCode();
                    String responseBody = EntityUtils.toString(response.getEntity());
                    if (statusCode != HttpStatus.OK.value()) {
                        throw new InvokeAPICheckException(statusCode, responseBody);
                    }
                    Thread.sleep(200);
                }
            }
        }
    }

    public static String generateStringFromTemplate(String templateRelativePath, Map<String, String> params)
            throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(templateRelativePath);
        Writer writer = new StringWriter();
        mustache.execute(writer, params).flush();
        return writer.toString();
    }

    public static String generateStringPayloadFromTemplate(String templateRelativePath, Map<String, Object> params)
            throws IOException {
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(templateRelativePath);
        Writer writer = new StringWriter();
        mustache.execute(writer, params).flush();
        return writer.toString();
    }

    /**
     * Invoke API GET with validation
     *
     * @param runner           Test action runner
     * @param apiKey           API Key
     * @param invokeUrl        Invoke URL
     * @param resource         API Resource
     * @param expectedResponse Expected response
     */
    public static void invokeApiGET(TestActionRunner runner, String apiKey, String invokeUrl, String resource,
            String expectedResponse) throws Exception {
        // Test API Invocation
        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions((http()
                        .client(invokeUrl)
                        .send()
                        .get(resource)
                        .message()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("API-Key", apiKey)),
                        http()
                                .client(invokeUrl)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(expectedResponse)));

        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * Invoke API GET with validation for internal endpoint testing.
     *
     * @param runner           Test action runner
     * @param apiKey           API Key
     * @param invokeUrl        Invoke URL
     * @param testSessionId    Test session ID
     * @param resource         API Resource
     * @param expectedResponse Expected response
     */
    public static void invokeApiGET(TestActionRunner runner, String apiKey, String invokeUrl, String testSessionId ,String resource,
                                    String expectedResponse) throws Exception {
        // Test API Invocation
        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions((http()
                                .client(invokeUrl)
                                .send()
                                .get(resource)
                                .message()
                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                .header("API-Key", apiKey))
                                .header("x-choreo-test-session-id", testSessionId),
                        http()
                                .client(invokeUrl)
                                .receive()
                                .response(HttpStatus.OK)
                                .message()
                                .type(MessageType.JSON)
                                .body(expectedResponse)));

        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * Invoke API POST with validation
     *
     * @param runner           Test action runner
     * @param apiKey           API Key
     * @param invokeUrl        Invoke URL
     * @param resource         API Resource
     * @param requestBody      Request payload
     * @param expectedResponse Expected response
     *
     */
    public static void invokeApiPOST(TestActionRunner runner, String apiKey, String invokeUrl, String resource,
            String requestBody, String expectedResponse,
            org.springframework.http.HttpStatus expectedHttpStatus) {
        // Test API Invocation
        runner.$(repeatOnError()
                .until("i = 20")
                .index("i")
                .autoSleep(30000)
                .actions((http()
                        .client(invokeUrl)
                        .send()
                        .post(resource)
                        .message()
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .body(requestBody)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .header("API-Key", apiKey)),
                        http()
                                .client(invokeUrl)
                                .receive()
                                .response(expectedHttpStatus)
                                .message()
                                .type(MessageType.JSON)
                                .body(expectedResponse)
                                ));
    }

    public static List<Environment> getEnvironments(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken, ChoreoComponent component) throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .orgUuid(component.getOrganization().getOrgUUID())
                .projectId(component.getProjectId()).build();

        return GraphQL.getEnvironments(runner, cpProjectsClient, accessToken, graphqlDTO);
    }

    public static List<Endpoint> getEndpoints(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken, ChoreoComponent component,
            ComponentDeploymentStatusDTO componentDeploymentStatusDTO) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        Map<String, String> argMap = new HashMap<>();
        argMap.put("componentId", component.getId());
        argMap.put("versionId", componentDeploymentStatusDTO.getVersionId());
        argMap.put("releaseId", componentDeploymentStatusDTO.getReleaseId());
        GraphQL.validateEndpointDeployment(runner, appServiceClient, accessToken, argMap);
        return GraphQL.getEndpoints(runner, appServiceClient, accessToken, argMap);
    }
    
    public static void validateEndpoints(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
                                              String accessToken, ChoreoComponent component,
                                              ComponentDeploymentStatusDTO componentDeploymentStatusDTO) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        Map<String,String> argMap = new HashMap<>();
        argMap.put("componentId", component.getId());
        argMap.put("versionId", componentDeploymentStatusDTO.getVersionId());
        argMap.put("releaseId", componentDeploymentStatusDTO.getReleaseId());
        GraphQL.validateEndpointDeployment(runner, appServiceClient, accessToken, argMap);
    }

    public static List<Endpoint> getEndpoints(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken, ChoreoComponent component, String environment) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        Map<String, String> argMap = new HashMap<>();
        argMap.put("componentId", component.getId());
        argMap.put("versionId", component.getLatestApiVersion().getId());
        argMap.put("releaseId", component.getReleaseIdForEnvironment(environment));
        GraphQL.validateEndpointDeployment(runner, appServiceClient, accessToken, argMap);
        List<Endpoint> endpoints = GraphQL.getEndpoints(runner, appServiceClient, accessToken, argMap);
        Assert.assertEquals(endpoints.size(), 1);

        return endpoints;
    }

    public static List<Endpoint> getEndpointList(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken, ChoreoComponent component, Environment environment) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        Map<String, String> argMap = new HashMap<>();
        argMap.put("componentId", component.getId());
        argMap.put("versionId", component.getLatestApiVersion().getId());
        argMap.put("releaseId", component.getReleaseIdForEnvironment(environment));
        List<Endpoint> endpoints = GraphQL.getEndpoints(runner, appServiceClient, accessToken, argMap);
        return endpoints;
    }

    public static Pair<String, KeyData> getInvokeInfo(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients,
            String accessToken, ChoreoComponent component, ComponentDeploymentStatusDTO statusDTO,
            List<Environment> environments) throws Exception {
        List<Endpoint> endpoints = getEndpoints(runner, citrusClients, accessToken, component, statusDTO);
        Endpoint endpoint = endpoints.get(0);
        String apimId = endpoint.getApimId();
        String invokeUrl = endpoint.getPublicUrl();

        Optional<Environment> matchingAPIMEnv = environments.stream()
                .filter(env -> env.getId().equals(statusDTO.getEnvironmentId())).findFirst();

        if (matchingAPIMEnv.isPresent()) {
            Environment apimEnv = matchingAPIMEnv.get();
            KeyData apiKey = ApiManager.getApiKey(runner, citrusClients.get(Endpoints.STS_ENDPOINT),
                    accessToken, apimId, ComponentUtils.getKeyType(apimEnv));
            return Pair.of(invokeUrl, apiKey);
        } else {
            throw new RuntimeException("Env id " + statusDTO.getEnvironmentId() +
                    " does not exist in the list of envs " + environments);
        }
    }

    /**
     * getInvokeInfoInternalEndpoint returns the API invocation data for internal endpoint testing.
     *
     * @param runner - TestNGCitrusSpringSupport runner
     * @param citrusClients - Map<Endpoints, HttpClient> citrusClients
     * @param accessToken - Access Token
     * @param component - Choreo component
     * @param statusDTO - Choreo component deployment status
     * @param environments - Choreo component environments
     * @return Pair<String, KeyData>
     * @throws Exception
     */
    public static Pair<String, KeyData> getInvokeInfoInternalEndpoint(TestNGCitrusSpringSupport runner,
                                                      Map<Endpoints, HttpClient> citrusClients,
                                                      String accessToken, ChoreoComponent component, ComponentDeploymentStatusDTO statusDTO,
                                                      List<Environment> environments) throws Exception {
        List<Endpoint> endpoints = getEndpoints(runner, citrusClients, accessToken, component, statusDTO);
        Endpoint endpoint = endpoints.get(0);
        String apimId = endpoint.getApimId();
        String invokeUrl = endpoint.getOrganizationUrl();

        Optional<Environment> matchingAPIMEnv = environments.stream()
                .filter(env -> env.getId().equals(statusDTO.getEnvironmentId())).findFirst();

        if (matchingAPIMEnv.isPresent()) {
            Environment apimEnv = matchingAPIMEnv.get();
            KeyData apiKey = ApiManager.getApiKey(runner, citrusClients.get(Endpoints.STS_ENDPOINT),
                    accessToken, apimId, ComponentUtils.getKeyType(apimEnv));
            return Pair.of(invokeUrl, apiKey);
        } else {
            throw new RuntimeException("Env id " + statusDTO.getEnvironmentId() +
                    " does not exist in the list of envs " + environments);
        }
    }

    private static Pair<Environment, String> getEnvironmentWithReleaseId(TestActionRunner runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            Constant.Environment env)
            throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .orgUuid(component.getOrganization().getOrgUUID())
                .projectId(component.getProjectId()).build();

        List<Environment> environments = GraphQL.getEnvironments(runner, cpProjectsClient, accessToken, graphqlDTO);

        Optional<Environment> matchingEnv = environments.stream().filter(e -> e.getName().equals(env.name()))
                .findFirst();

        String releaseId = component.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);

        if (env == Constant.Environment.Production) {
            releaseId = component.getReleaseIdForEnvironment(Constant.PROD_ENVIRONMENT);
        }

        if (matchingEnv.isPresent()) {
            return Pair.of(matchingEnv.get(), releaseId);
        } else {
            throw new RuntimeException("Env " + env.name() + " does not exist");
        }
    }

    public static void updateEnvironments(List<Environment> existingEnvs, List<Environment> addionalEnvironmentInfo) {
        for (Environment environment : addionalEnvironmentInfo) {
            Optional<Environment> first = existingEnvs.stream()
                    .filter(e -> e.getId().equals(environment.getId())).findFirst();

            if (first.isPresent()) {
                Environment existingEnv = first.get();
                existingEnv.setNamespace(environment.getNamespace());
            }
        }
    }

    public static List<ObservabilityIdInformation> getObservabilityIds(TestActionRunner runner,
            Map<Endpoints, HttpClient> citrusClients,
            String accessToken, ChoreoComponent component)
            throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        String devReleaseId = component.getReleaseIdForEnvironment(Constant.DEV_ENVIRONMENT);
        String prodReleaseId = component.getReleaseIdForEnvironment(Constant.PROD_ENVIRONMENT);

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().releaseIds(devReleaseId + "," + prodReleaseId).build();
        return GraphQL.getObservabilityIds(runner, cpProjectsClient, accessToken, graphqlDTO);
    }

    public static void verifyAuditLogs(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken) throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        AuditLogsService.verifyAuditLogs(runner, choreoCPTestClient, accessToken);
    }

    public static void verifyDataPlanes(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken) throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        AuditLogsService.verifyDataPlanes(runner, choreoCPTestClient, accessToken);
    }

    public static void verifyCloudPlanes(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken) throws Exception {
        HttpClient choreoCPTestClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        AuditLogsService.verifyCloudPlanes(runner, choreoCPTestClient, accessToken);
    }

    public static void verifyProjectLevelDPLogs(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusDPClients,
            String accessToken, ChoreoProject project, ChoreoComponent choreoComponent, Environment env)
            throws Exception {
        DPObsApiService.getProjectLogs(runner, citrusDPClients, accessToken,
                project, choreoComponent, env, false);
    }

    public static void verifyComponentLevelDPLogs(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusDPClients,
            String accessToken, ChoreoProject project, ChoreoComponent component, Environment env) throws Exception {
        DPObsApiService.getComponentLogs(runner, citrusDPClients, accessToken, project,
                component, env, false);
    }

    public static void verifyGatewayDPLogs(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusDPClients,
            String accessToken, ChoreoProject project, ChoreoComponent component, Environment env) throws Exception {
        DPObsApiService.getGatewayLogs(runner, citrusDPClients, accessToken, project,
                component, env, false);
    }

    public static void verifyProjectLevelDPLogsLive(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusDPClients,
            String accessToken, ChoreoProject project, ChoreoComponent choreoComponent, Environment env)
            throws Exception {
        DPObsApiService.getProjectLogs(runner, citrusDPClients, accessToken,
                project, choreoComponent, env, true);
    }

    public static void verifyProjectLevelDPMetrics(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusDPClients,
            String accessToken, ChoreoProject project, ChoreoComponent choreoComponent, Environment env)
            throws Exception {
        DPObsApiService.getProjectMetrics(runner, citrusDPClients, accessToken,
                project, choreoComponent, env, true);
    }

    public static void verifyComponentLevelAppMetrics(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusDPClients,
            String accessToken, ChoreoComponent choreoComponent, Environment env, ChoreoProject project)
            throws Exception {
        DPObsApiService.getComponentAppMetrics(runner, citrusDPClients, accessToken,
                choreoComponent, env, project);
    }

    public static void verifyComponentLevelDPLogsLive(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusDPClients,
            String accessToken, ChoreoProject project, ChoreoComponent component, Environment env) throws Exception {
        DPObsApiService.getComponentLogs(runner, citrusDPClients, accessToken, project,
                component, env, true);
    }

    public static void verifySystemMetricsLive(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusDPClients,
            String accessToken, ChoreoProject project, ChoreoComponent component, Environment env) throws Exception {
        DPObsApiService.getSystemMetrics(runner, citrusDPClients, accessToken, project,
                component, env, true);
    }

    public static void verifyGatewayDPLogsLive(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients,
            String accessToken, ChoreoProject project, ChoreoComponent component, Environment env) throws Exception {
        DPObsApiService.getGatewayLogs(runner, citrusClients, accessToken, project,
                component, env, true);
    }

    public static RevisionWrapper getRevisions(TestActionRunner runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken, String apiId, String orgUuid) throws Exception {
        HttpClient httpClient = citrusClients.get(Endpoints.STS_ENDPOINT);

        RevisionWrapper revisionCount = ApiManager.getRevisionCount(runner, httpClient, accessToken, apiId, orgUuid);
        return revisionCount;

    }

    public static void createNewVersion(TestNGCitrusSpringSupport runner, Map<Endpoints, HttpClient> citrusClients,
            String accessToken, GraphqlDTO graphqlDTO) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        GraphQL.createNewVersion(runner, appServiceClient, accessToken, graphqlDTO);
        Component.waitForComponentCreationSuccess(runner, appServiceClient, accessToken, graphqlDTO.getProjectId(),
                graphqlDTO.getComponentId());
    }

    public static ComponentDeploymentStatusDTO deployComponentInBranch(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            List<Environment> environments, ComponentFlavour componentFlavour, String branchName,
            BalConfig... balconfigs) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, appServiceClient, component.getId(),
                accessToken, branchName);

        Environment environment = environments.get(0);

        if (componentFlavour.equals(ComponentFlavour.STANDARD)) {
            Component.triggerConfigurableGeneration(runner, appServiceClient, component, commitHistory,
                    component.getBranch());
            ConfigManagement.addConfiguration(runner, appServiceClient, component, commitHistory, environment,
                    balconfigs);
        }

        Commit latestCommit = Commit.getLatestCommit(commitHistory);
        String shaDate = latestCommit.getAuthor().getDate();
        String sha = latestCommit.getSha();

        String componentId = component.getId();
        ApiVersion apiVersion = component.getLatestApiVersion();
        String latestVersionId = apiVersion.getId();

        String devEnvIdToDeploy = environment.getId();
        String branch = component.getRepository().getBranch();

        GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).latestVersionId(latestVersionId)
                .devEnvIdToDeploy(devEnvIdToDeploy).branch(branch).sha(sha).shaDate(shaDate).build();

        // Deploy component
        GraphQL.deployComponent(runner, appServiceClient, accessToken, graphqlDTO);

        GraphQL.getDeploymentStatusByVersion(runner, appServiceClient, accessToken, graphqlDTO);

        ChoreoOrganization org = component.getOrganization();
        graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle())
                .orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(devEnvIdToDeploy)
                .build();

        Map<String, String> responseParams = new HashMap<>();
        responseParams.put("environmentId", devEnvIdToDeploy);
        responseParams.put("sha", sha);
        responseParams.put("versionId", latestVersionId);

        return GraphQL.getComponentDeploymentStatus(runner, appServiceClient, accessToken, graphqlDTO,
                responseParams);
    }

    public static List<ComponentDeploymentStatusDTO> promoteComponentInBranch(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            List<Environment> environments,
            ComponentFlavour componentFlavour, String branchName, BalConfig... balconfigs) throws Exception {
        HttpClient cpProjectsClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);

        List<Commit> commitHistory = GraphQL.getCommitHistory(runner, cpProjectsClient, component.getId(),
                accessToken, branchName);

        int srcEnvIndex = 0;
        int destEnvIndex = 1;

        List<ComponentDeploymentStatusDTO> deploymentStatus = new ArrayList<>();

        while (destEnvIndex < environments.size()) {
            Environment srcEnv = environments.get(srcEnvIndex);
            Environment destEnv = environments.get(destEnvIndex);

            if (componentFlavour.equals(ComponentFlavour.STANDARD)) {
                ConfigManagement.addConfiguration(runner, cpProjectsClient, component, commitHistory, destEnv,
                        balconfigs);
            }

            String componentId = component.getId();
            ApiVersion apiVersion = component.getLatestApiVersion();
            String latestVersionId = apiVersion.getId();
            String sourceReleaseId = component.getReleaseIdForEnvironment(srcEnv);
            String latestAppEnvId = destEnv.getId();

            GraphqlDTO graphqlDTO = GraphqlDTO.builder().componentId(componentId).apiVersionId(latestVersionId)
                    .sourceReleaseId(sourceReleaseId).targetEnvironmentId(latestAppEnvId).build();
            GraphQL.promoteComponent(runner, cpProjectsClient, accessToken, graphqlDTO);

            ChoreoOrganization org = component.getOrganization();
            graphqlDTO = GraphqlDTO.builder().componentId(componentId).orgHandler(org.getOrgHandle())
                    .orgUuid(org.getOrgUUID()).versionId(latestVersionId).environmentId(latestAppEnvId).build();

            Commit latestCommit = Commit.getLatestCommit(commitHistory);
            String sha = latestCommit.getSha();

            Map<String, String> responseParams = new HashMap<>();
            responseParams.put("environmentId", latestAppEnvId);
            responseParams.put("sha", sha);
            responseParams.put("versionId", latestVersionId);

            deploymentStatus.add(GraphQL.getComponentDeploymentStatus(runner, cpProjectsClient, accessToken, graphqlDTO,
                    responseParams));

            ++srcEnvIndex;
            ++destEnvIndex;
        }

        return deploymentStatus;
    }

    public static Pair<Boolean, Integer> testDeploymentWithRateLimit(String invokeURL, String apiKey,
            int repititionCount) throws Exception {

        // Rate limiting counter resets based on the system clock.
        long timeRemainingTillNextMinute = 60000 - (System.currentTimeMillis() % 60000);
        if (timeRemainingTillNextMinute < 15000) {
            Thread.sleep(timeRemainingTillNextMinute + 5000);
        }

        boolean isRateLimitExceeded = false;
        int count = 0;
        long startTime;
        long endTime;

        // Repeat the check until the rate limit is exceeded or all requests are sent
        // within the same minute
        while (!isRateLimitExceeded) {
            count = 0;
            startTime = System.currentTimeMillis();
            for (int i = 0; i < repititionCount; i++) {
                Response dev = HttpClientUtil.httpGET(invokeURL, "", apiKey);
                count++;
                if (dev.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS.value()) {
                    isRateLimitExceeded = true;
                    break;
                }
                Thread.sleep(500);
            }
            endTime = System.currentTimeMillis();

            // Break the loop if all requests are sent within the same minute
            if (endTime / 60000 == startTime / 60000) {
                break;
            }
        }

        return Pair.of(isRateLimitExceeded, count);
    }

    public static String getKeyType(ProxyEnvironment proxyEnvironment, List<Environment> choreoEnvironment) {
        String envMappingId = proxyEnvironment.getId();

        for (Environment environment : choreoEnvironment) {
            if (environment.getId().equals(envMappingId)) {
                return getKeyType(environment);
            }
        }

        return "";
    }

    public static String getKeyType(Environment environment) {
        if (environment.isCritical()) {
            return "Production";
        }

        return "Development";
    }

    public static void validateEndpointExistence(TestNGCitrusSpringSupport runner,
            Map<Endpoints, HttpClient> citrusClients, String accessToken, ChoreoComponent component,
            Environment environment) throws Exception {
        List<Endpoint> endpoints = getEndpointList(runner, citrusClients, accessToken, component, environment);
        String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
        HttpClient appServiceClient = citrusClients.get(Endpoints.STS_ENDPOINT);

        for (Endpoint endpoint : endpoints) {
            String resourceURL = APIS_ENDPOINT.concat("/").concat(endpoint.getApimId()).concat("?")
                    .concat(Constant.ORGANIZATION_ID).concat("=").concat(orgUuid);
            isEndpointExists(runner, appServiceClient, resourceURL, accessToken);
        }
    }

    public static void isEndpointExists(TestNGCitrusSpringSupport runner, HttpClient client, String path,
            String accessToken) {
        runner.variable("isEndpointContextExists", false);
        runner.$(repeatOnError()
                .until("(i = 5) or ( ${isEndpointContextExists} = true )")
                .index("i")
                .autoSleep(5000)
                .actions(
                        http()
                                .client(client)
                                .send()
                                .get(path)
                                .message()
                                .header(HttpHeaders.AUTHORIZATION, accessToken)
                                .accept(String.valueOf(MediaType.APPLICATION_JSON)),
                        http().client(client)
                                .receive()
                                .response()
                                .message()
                                .validate((message, context) -> {
                                    int code = (int) message.getHeader(HttpMessageHeaders.HTTP_STATUS_CODE);
                                    if (code != HttpStatus.OK.value()) {
                                        throw new ValidationException(
                                                "Too many successive calls with response code != 200");
                                    }
                                })));
    }

    public static KeyGenResponseDTO generateKeys(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, HashMap<String, Object> keygenRequest)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return Component.generateKeys(runner, client, projectId, componentId, environmentId, keygenRequest);
    }

    public static KeyGenResponseDTO regenerateKeys(TestActionRunner runner, HttpClient client,
            String projectId, String componentId, String environmentId, String oAuthAppId)
            throws TokenRetrievalException, IOException, URISyntaxException {

        return Component.regenerateKeysets(runner, client, projectId, componentId, environmentId, oAuthAppId);
    }

    public static void addExternalIdpKeys(TestActionRunner runner, HttpClient client, String projectId,
            String componentId, String environmentId, HashMap<String, Object> keyMappingRequest,
            HttpStatus expectedStatus)
            throws TokenRetrievalException, IOException, URISyntaxException {

        Component.addExternalIdpKeys(runner, client, projectId, componentId, environmentId, keyMappingRequest,
                expectedStatus);
    }

    public static ChoreoComponent createComponentVersion(
            TestNGCitrusSpringSupport runner, Map<Endpoints,HttpClient> citrusClients, String accessToken,
            ChoreoComponent choreoComponent, String version, String branchName) throws Exception {
        HttpClient appServiceClient = citrusClients.get(Endpoints.CHOREO_NEW_APP_SERVICE_ENDPOINT);
        GraphqlDTO graphqlDTO = GraphqlDTO.builder()
                .orgUuid(choreoComponent.getOrgId())
                .componentId(choreoComponent.getId())
                .apiVersion(version)
                .branch(branchName)
                .description(choreoComponent.getDescription())
                .build();
        CreateNewDeploymentTrackResponseDTO newDeploymentTrack = GraphQL.createNewDeploymentTrack(runner, appServiceClient, accessToken, graphqlDTO);
        List<ApiVersion> apiVersions = new ArrayList<>();
        for (ApiVersion existingVersion: choreoComponent.getApiVersions()) {
            existingVersion.setLatest(false);
            apiVersions.add(existingVersion);
        }
        ApiVersion latestApiVersion = new ApiVersion();
        latestApiVersion.setLatest(true);
        latestApiVersion.setId(newDeploymentTrack.getId());
        latestApiVersion.setAppEnvVersions(apiVersions.get(0).getAppEnvVersions());
        apiVersions.add(latestApiVersion);

        choreoComponent.setApiVersions(apiVersions);
        return choreoComponent;
    }

    public static void configureWebappShortUrl(TestNGCitrusSpringSupport runner, String accessToken, 
            ChoreoComponent component, List<Environment> environments, String shortUrl) throws Exception {

        String componentId = component.getId();
        Environment prodEnv = component.getEnvironment(environments.stream().toArray(Environment[] ::new), 
            Constant.Environment.Production);
        String prodReleaseId = component.getReleaseIdForEnvironment(prodEnv);

        String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);

        String projectId = component.getProjectId();

        DevopsPortalApi.configureWebappShortUrl(runner, accessToken, componentId, prodReleaseId, orgUuid, projectId, 
            shortUrl);
    }

    /**
     * generateTestSession generates a test session for a given internal endpoint for a given user.
     *
     * @param runner - TestNGCitrusSpringSupport runner
     * @param citrusClients - Map<Endpoints,HttpClient> citrusClients
     * @param accessToken - Access Token
     * @param choreoComponent - Choreo component
     * @param environments - Choreo component environment
     * @param endpoints - Choreo component endpoints
     * @param userIdpId - User IDP ID
     * @return TestSessionResponse
     * @throws Exception
     */
    public static TestSessionResponse generateTestSession(TestNGCitrusSpringSupport runner, Map<Endpoints,HttpClient> citrusClients,
                                                          String accessToken, ChoreoComponent choreoComponent,
                                                          List<Environment> environments, List<Endpoint> endpoints,
                                                          String userIdpId) throws Exception {
        HttpClient proxyDeployerClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String componentId = choreoComponent.getId();
        String environmentId = environments.get(0).getId();
        String endpointId = endpoints.get(0).getId();

        return ProxyDeployer.generateTestSession(runner, proxyDeployerClient, accessToken, componentId, environmentId,
                userIdpId, endpointId, "Organization");
    }

    /**
     * deleteTestSession deletes the test session for a given session id.
     *
     * @param runner - TestNGCitrusSpringSupport runner
     * @param citrusClients - Map<Endpoints,HttpClient> citrusClients
     * @param accessToken - Access Token
     * @param choreoComponent - Choreo component
     * @param environments - Choreo component environment
     * @param endpoints - Choreo component endpoints
     * @param userIdpId - User IDP ID
     * @param sessionId - Test session ID
     */
    public static void deleteTestSession(TestNGCitrusSpringSupport runner, Map<Endpoints,HttpClient> citrusClients,
                                         String accessToken, ChoreoComponent choreoComponent,
                                         List<Environment> environments, List<Endpoint> endpoints,
                                         String userIdpId, String sessionId) {
        HttpClient proxyDeployerClient = citrusClients.get(Endpoints.CHOREO_ENDPOINT);
        String componentId = choreoComponent.getId();
        String environmentId = environments.get(0).getId();
        String endpointId = endpoints.get(0).getId();

        ProxyDeployer.deleteTestSession(runner, proxyDeployerClient, accessToken, componentId, environmentId,
                userIdpId, endpointId, sessionId);
    }

    /**
     * Enables Managed Authentication local development for a component.
     *
     * @param runner                        The TestActionRunner instance.
     * @param client                        The HttpClient instance.
     * @param projectId                     The ID of the project.
     * @param componentId                   The ID of the component.
     * @param releaseId                     The ID of the release.
     * @param localDevelopmentConfigureRequest The request object containing the necessary information for enabling local development.
     * @throws TokenRetrievalException      If an error occurs while retrieving the token.
     * @throws IOException                  If an I/O error occurs.
     * @throws URISyntaxException           If the URI syntax is incorrect.
     */
    public static void configureLocalDevelopmentForManagedAuthentication(TestActionRunner runner, HttpClient client, String projectId,
            String componentId, String releaseId, HashMap<String, Object> localDevelopmentConfigureRequest, HttpStatus expectedStatus)
            throws TokenRetrievalException, IOException, URISyntaxException {

        Component.configureLocalDevelopmentForManagedAuthentication(runner, client, projectId, componentId, releaseId,
                localDevelopmentConfigureRequest, expectedStatus);
    }
}
