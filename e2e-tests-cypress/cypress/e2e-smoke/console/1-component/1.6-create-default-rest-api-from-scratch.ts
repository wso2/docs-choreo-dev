import { GraphQL } from "../../../support/console/apis/graphql";
import { TestHelper } from "../../../support/console/pages/component/common/test-helper";
import { ComponentDeployPage } from "../../../support/console/pages/component/component-deploy";
import { ComponentListingPage } from "../../../support/console/pages/component/component-listing-page";
import { ComponentAPILifecycle } from "../../../support/console/pages/component/component-manage-page";
import { ComponentOverviewPage } from "../../../support/console/pages/component/component-overview-page";
import { Enums } from "../../../support/console/enums";
import { ChoreoHomePage } from "../../../support/console/pages/home/home-page";
import { LoginPage } from "../../../support/console/pages/login-page";
import { ProjectListingPage } from "../../../support/console/pages/projects/projects-listing-page";
import { Utils } from "../../../support/console/utils";
import { ComponentData } from "../../../support/interfaces/component-data";
import { GraphQLQueryBuilder } from "../../../support/console/apis/gql-query-builder";
import { GitHub } from "../../../support/github/github";



const dps = Object.values(Enums.Region)

dps.forEach(dp => {
  describe(`Verify BYOR functionality in region ${dp}`, () => {
    const PROJECT_DESCRIPTION = "Internal API Test";
    const PROJECT_NAME = Utils.generateProjectName();
    const REST_API_NAME = Utils.generateComponentName("byor");
    const REPO_NAME = Utils.generateComponentName("repo");
    const RESOURCE_NAME = "greeting";
    const RESOURCE_NAME1 = "hi";
    const PARAM_NAME = "name";
    const PARAM_VALUE = "World";
    const MATCHING_STRING = "Hello, " + PARAM_VALUE;

    const PARAM_NAME1 = "name";
    const PARAM_VALUE1 = "John";
    const MATCHING_STRING1 = "Hi, " + PARAM_VALUE1;
    const queryParameters1 = [{ key: PARAM_NAME, value: PARAM_VALUE }];
    const queryParameters2 = [{ key: PARAM_NAME1, value: PARAM_VALUE1 }];


    before(() => {
      LoginPage.login();
      GitHub.deleteWebhooks("greeting-rest-api")
    });

    after(() => {
      ChoreoHomePage.logout();
    });

    it("Verify REST API component creation", () => {
      let componentData: ComponentData = {
        componentName: REST_API_NAME,
        displayType: Enums.DisplayType.restAPI,
        accessibility: Enums.Accessibility.EXTERNAL,
        projectName: PROJECT_NAME,
        triggerChannels: "",
        triggerId: null,
        srcGitRepoUrl: "https://github.com/choreo-test-apps/greeting-rest-api",
        initializeAsBallerinaProject: false,
        repositoryType: Enums.RepoType.UserManagedNonEmpty,
        repositorySubPath: "",
        sampleTemplate: "",
      };
      ProjectListingPage.createNewProject(
        PROJECT_NAME,
        PROJECT_DESCRIPTION,
        dp
      );
      GraphQL.createComponent(PROJECT_NAME, REPO_NAME, componentData, GraphQLQueryBuilder.getRestComponentCreationQuery)
    });

    it("Deploy component", () => {
      ComponentListingPage.visitToAComponent(REST_API_NAME);
      ComponentOverviewPage.navigateToDeploy();
      ComponentDeployPage.deployToDev();
    });

    it("Verify test functionality of root resource in dev on swagger", () => {
      ComponentOverviewPage.navigateToTest(true);
      TestHelper.testOnSwagger(
        Enums.Environment.DEVELOPMENT,
        RESOURCE_NAME,
        PARAM_NAME,
        PARAM_VALUE
      ).then((res) => {
        expect(res.response).to.be.eq(MATCHING_STRING);
        expect(res.statusCode).to.be.eq("200");
      });
    });

    it("Verify test functionality of root resource in dev on curl", () => {
      TestHelper.testOnCurl(
        Enums.Environment.DEVELOPMENT,
        Enums.HTTPMethod.GET,
        RESOURCE_NAME,
        queryParameters1
      ).then((curl) => {
        Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
          expect(res.body).equal(MATCHING_STRING);
          expect(res.status).equal(200);
        });
      });
    });

    it("Verify component promote to prod", () => {
      ComponentOverviewPage.navigateToDeploy();
      ComponentDeployPage.promoteToProd();
    });



    it("Verify test functionality of root resource in prod on swagger", () => {
      ComponentOverviewPage.navigateToTest();
      TestHelper.testOnSwagger(
        Enums.Environment.PRODUCTION,
        RESOURCE_NAME,
        PARAM_NAME,
        PARAM_VALUE
      ).then((res) => {
        expect(res.response).to.be.eq(MATCHING_STRING);
        expect(res.statusCode).to.be.eq("200");
      });
    });

    it("Verify test functionality of root resource in prod on curl", () => {
      TestHelper.testOnCurl(
        Enums.Environment.PRODUCTION,
        Enums.HTTPMethod.GET,
        RESOURCE_NAME,
        queryParameters1
      ).then((curl) => {
        Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
          expect(res.body).equal(MATCHING_STRING);
          expect(res.status).equal(200);
        });
      });
    });

    it("Apply configs to dev", () => {
      ComponentOverviewPage.navigateToManage();
      ComponentAPILifecycle.selectSetting();
      ComponentAPILifecycle.selectResources();
      ComponentAPILifecycle.selectEnvironment(Enums.Environment.DEVELOPMENT);
      ComponentAPILifecycle.editResource();
      ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
      ComponentAPILifecycle.applyConfiguration();
      ComponentAPILifecycle.verifyDevRevision().should(
        "eq",
        Enums.Environment.DEVELOPMENT
      );
    });

    it("Apply configs to prod", () => {
      ComponentAPILifecycle.selectEnvironment(Enums.Environment.PRODUCTION);
      ComponentAPILifecycle.editResource();
      ComponentAPILifecycle.disableResourceSecurity(RESOURCE_NAME);
      ComponentAPILifecycle.applyConfiguration();
    });

    it("Verify resource access without the token in dev", () => {
      ComponentOverviewPage.navigateToTest();
      TestHelper.testOnCurl(
        Enums.Environment.DEVELOPMENT,
        Enums.HTTPMethod.GET,
        RESOURCE_NAME,
        queryParameters1
      ).then((curl) => {
        Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
          expect(res.body).equal(MATCHING_STRING);
          expect(res.status).equal(200);
        });
      });
    });

    it("Verify resource access without the token in prod", () => {
      TestHelper.testOnCurl(
        Enums.Environment.PRODUCTION,
        Enums.HTTPMethod.GET,
        RESOURCE_NAME
      ).then((curl) => {
        Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
          expect(res.body).equal(MATCHING_STRING);
          expect(res.status).equal(200);
        });
      });
    });

    it("Verify new version", () => {
      ComponentOverviewPage.navigateToDeploy();
      ComponentDeployPage.addNewVersion()
      ComponentDeployPage.deployToDev();
    })

    it("Verify test functionality of root resource in dev on swagger", () => {
      ComponentOverviewPage.navigateToTest(true);
      TestHelper.testOnSwagger(
        Enums.Environment.DEVELOPMENT,
        RESOURCE_NAME1,
        PARAM_NAME1,
        PARAM_VALUE1
      ).then((res) => {
        expect(res.response).to.be.eq(MATCHING_STRING1);
        expect(res.statusCode).to.be.eq("200");
      });
    });

    it("Verify test functionality of root resource in dev on curl", () => {
      TestHelper.testOnCurl(
        Enums.Environment.DEVELOPMENT,
        Enums.HTTPMethod.GET,
        RESOURCE_NAME1,
        queryParameters2
      ).then((curl) => {
        Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
          expect(res.body).equal(MATCHING_STRING1);
          expect(res.status).equal(200);
        });
      });
    });

    it("Verify component promote to prod", () => {
      ComponentOverviewPage.navigateToDeploy();
      ComponentDeployPage.promoteToProd();
    });



    it("Verify test functionality of root resource in prod on swagger", () => {
      ComponentOverviewPage.navigateToTest();
      TestHelper.testOnSwagger(
        Enums.Environment.PRODUCTION,
        RESOURCE_NAME1,
        PARAM_NAME1,
        PARAM_VALUE1
      ).then((res) => {
        expect(res.response).to.be.eq(MATCHING_STRING1);
        expect(res.statusCode).to.be.eq("200");
      });
    });

    it("Verify test functionality of root resource in prod on curl", () => {
      TestHelper.testOnCurl(
        Enums.Environment.PRODUCTION,
        Enums.HTTPMethod.GET,
        RESOURCE_NAME1,
        queryParameters2
      ).then((curl) => {
        Utils.sendGetRequest(curl.url, curl.headers).then((res) => {
          expect(res.body).equal(MATCHING_STRING1);
          expect(res.status).equal(200);
        });
      });
    });

    it("Verify suspending Prod deployed component", () => {
      ComponentOverviewPage.navigateToDeploy();
      ComponentDeployPage.stopAllDeployment();
    });
  });

})

