import { Enums } from "../../../../commons/enums";
import { Types } from "../../../../commons/types";
import { login } from "../../login/login";
import { Component } from "../component";
import { _ProxyDeployment } from "./proxy-deployment";
import { _ProxyDevelop } from "./proxy-develop";
import { _ProxyManagement } from "./proxy-management";
import { _ProxyOverview } from "./proxy-overview";
import { _ProxyTest } from "./proxy-test";

export class Proxy extends Component {
  private endpointUrl: string;
  private basePath: string;

  private overview = new _ProxyOverview();
  private develop = new _ProxyDevelop();
  private deployment = new _ProxyDeployment();
  private test = new _ProxyTest();
  private manage = new _ProxyManagement();

  constructor(
    name: string,
    version: string,
    basePath: string,
    endpointUrl: string,
    componentUrl: string
  ) {
    super(name, version, componentUrl.toString());

    this.basePath = basePath;
    this.endpointUrl = endpointUrl;
  }

  getBasePath() {
    return this.basePath;
  }

  getEndpointUrl() {
    return this.endpointUrl;
  }

  removeResources(resourceIds: string[]) {
    this.develop.removeResources(this, resourceIds);
  }

  deploy() {
    this.deployment.deploy(this);
  }

  promote() {
    this.deployment.promote(this);
  }

  testSwaggerConsole(
    environment: Enums.Environment,
    resource: string,
    key?: string,
    value?: string
  ) {
    return this.test.testSwaggerConsole(
      this,
      environment,
      resource,
      key,
      value
    );
  }

  testCurl(
    environment: Enums.Environment,
    method: Enums.HTTPMethod,
    resource: string
  ) {
    return this.test.testCurl(this, environment, method, resource);
  }

  addVersion() {
    this.deployment.addNewVersion(this);
  }

  addResources(resourcePaths: Types.ResourcePath[]) {
    this.develop.addResources(this, resourcePaths);
  }

  publish() {
    this.manage.changeLifeCycleState(this, Enums.LifeCycleState.Publish);
  }

  navigateToDevPortal() {
    this.overview.navigateToDevPortal(this, "choreoe2etest");
  }

  stop(env: Enums.Environment.DEVELOPMENT | Enums.Environment.PRODUCTION) {
    this.deployment.stop(env);
  }
}
