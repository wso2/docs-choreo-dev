import { ComponentDeployPage } from '../../../support/console/pages/component/component-deploy';
import { ComponentDevelopPage } from '../../../support/console/pages/component/component-develop-page';
import { ComponentAPILifecycle } from '../../../support/console/pages/component/component-manage-page';
import { ComponentOverviewPage } from '../../../support/console/pages/component/component-overview-page';
import { ComponentTestPage } from '../../../support/console/pages/component/component-test-page';
import { Curl } from '../../../support/console/pages/component/UI-components/curl-component';
import { Environment } from '../../../support/console/pages/enum/environment';
import { HTTPMethod } from '../../../support/console/pages/enum/http-method-enum';
import { HomePage } from '../../../support/console/pages/home/home-page';
import { LoginPage } from '../../../support/console/pages/login-page';
import { ProjectOverviewPage } from '../../../support/console/pages/projects/project-overview';
import { ProjectListingPage } from '../../../support/console/pages/projects/projects-listing-page';
import { RestAPITemplate } from '../../../support/console/pages/templates/rest-api-temp';
import { Utils } from '../../../support/console/utils';

describe('Verify resources security configuration', () => {
  const FILE_ID = 'resource-security-configuration';
  const COMPONENT_NAME = 'covid stat api';
  const COMPONENT_DESCRIPTION = 'covid daily stats';
  const PROJECT_DESCRIPTION = 'Covid stats project';
  const PROJECT_NAME = Utils.generateProjectName();
  const queryParameters = [{ key: 'name', value: 'dasun' }];

  before(() => LoginPage.loginToChoreo(FILE_ID));

  it('Verify REST API component creation', () => {
    ProjectListingPage.createNewProject(
      PROJECT_NAME,
      PROJECT_DESCRIPTION,
      FILE_ID
    );
    ProjectOverviewPage.addNewComponent();
    RestAPITemplate.selectHttpAPITemplate();
    RestAPITemplate.createApiFromScratch(
      COMPONENT_NAME,
      COMPONENT_DESCRIPTION,
      FILE_ID
    );
  });


  it('Verify component deployment', () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.deploy();
    ComponentDeployPage.isDeploymentSuccessful().should('be.visible');
    ComponentDeployPage.verifyDevInvokeURL().should('not.be.null');
  });

  it('Verify the revision in dev', () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.verifyDevRevision().should('eq', 'Development');
  });

  it('Verify dev deployment', () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.DEVELOPMENT);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.addQueryParameter(queryParameters);
    Curl.getRequestComponents(FILE_ID, Environment.DEVELOPMENT).then((curl) =>
      Utils.sendRequest(curl.method, curl.url, curl.headers).then((res) => {
        expect(res.body).equal('Hello, dasun');
        expect(res.status).equal(200);
      })
    );
  });

  it('Verify security disable in dev', () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity(
      '/sayHello'
    );
  });

  it('Verify resource access without the token in dev', () => {
    Curl.getRequestComponents(FILE_ID, Environment.DEVELOPMENT).then((curl) =>
      Utils.sendRequest(curl.method, curl.url).then((res) => {
        expect(res.body).equal('Hello, dasun');
        expect(res.status).equal(200);
      })
    );
  });

  it('Verify component promote to prod', () => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.promoteToProd();
    ComponentDeployPage.verifyProdInvokeURL().should('not.be.null');
  });

  it('Verify the revision in prod', () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.manageLifecycle();
    ComponentAPILifecycle.verifyProdRevision().should('eq', 'Production');
  });

  it('Verify prod deployment', () => {
    ComponentOverviewPage.navigateToTest();
    ComponentTestPage.selectCurl();
    Curl.selectEnvironment(Environment.DEVELOPMENT);
    Curl.selectMethod(HTTPMethod.GET);
    Curl.addQueryParameter(queryParameters);
    Curl.getRequestComponents(FILE_ID, Environment.PRODUCTION).then((curl) =>
      Utils.sendRequest(curl.method, curl.url, curl.headers).then((res) => {
        expect(res.body).equal('Hello, dasun');
        expect(res.status).equal(200);
      })
    );
  });

  it('Verify security disable in prod', () => {
    ComponentOverviewPage.navigateToManage();
    ComponentAPILifecycle.selectSetting();
    ComponentAPILifecycle.selectResources();
    ComponentAPILifecycle.editResource();
    ComponentAPILifecycle.disableResourceSecurity(
      '/sayHello',
      Environment.PRODUCTION
    );
  });

  it('Verify resource access without the token in prod', () => {
    Curl.getRequestComponents(FILE_ID, Environment.PRODUCTION).then((curl) =>
      Utils.sendRequest(curl.method, curl.url).then((res) => {
        expect(res.body).equal('Hello, dasun');
        expect(res.status).equal(200);
      })
    );
  });

  after(() => {
    ComponentOverviewPage.navigateToDeploy();
    ComponentDeployPage.stopAllDeployment();
    HomePage.logout(FILE_ID);
  });
});
