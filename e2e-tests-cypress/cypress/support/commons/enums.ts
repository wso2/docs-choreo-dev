/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

export namespace Enums {
  export enum PolicyType {
    setHeader = "Set Header (2.0.0)",
  }

  export enum Flow {
    REQUEST = "/in-flow",
    RESPONSE = "/out-flow",
    ERROR = "/fault-flow",
  }

  export enum DisplayType {
    restAPI = "restAPI",
    proxy = "proxy",
    webhook = "webhook",
    graphql = "graphql",
    manualTrigger = "manualTrigger",
    scheduledTask = "scheduledTask",
    byocRestApi = "byocRestApi",
    ballerinaService = "ballerinaService",
    byocService = "byocService",
    byocWebAppsDockerfileLess = "byocWebAppsDockerfileLess",
    buildpackTestRunner = "buildpackTestRunner",
  }

  export enum Accessibility {
    EXTERNAL = "external",
    INTERNAL = "internal",
    NONE = "none",
  }

  export enum Environment {
    DEVELOPMENT = "Development",
    PRODUCTION = "Production",
    STAGING = "Staging",
    SANDBOX = "Sandbox",
  }

  export enum ApiTryoutKeyType {
    TEST_KEY = "testKey",
    APPLICATION_KEY = "applicationKey",
    API_KEY = "apiKey",

  }

  export enum HTTPMethod {
    GET = "GET",
    POST = "POST",
    DELETE = "DELETE",
    PUT = "PUT",
    HEAD = "HEAD",
    TRACE = "TRACE",
  }

  export enum ConnectorAudience {
    PRIVATE = "private",
  }

  export enum Region {
    EU = "EU",
    US = "US",
    IND = "IND",
  }

  export enum Perspective {
    IDEVP = "perspective-pickeridevp",
    APIM = "perspective-pickerapim",
  }

  export enum ComponentType {
    MI_REST_API = "miRestApi",
    BYOC_REST_API = "byocRestApi",
    MI_API_SERVICE = "miApiService",
  }

  export enum RepoType {
    UserManagedEmpty = "UserManagedEmpty",
    UserManagedNonEmpty = "UserManagedNonEmpty",
  }

  export enum DeploymentStages {
    CODE_GEN = "CODE_GEN",
    PROXY_DEPLOY = "PROXY_DEPLOY",
    DEPLOY = "DEPLOY",
  }

  export enum ResponseStatus {
    success = "success",
    completed = "completed",
    failed = "failed",
    failure = "failure",
    error = "error",
    Error = "Error",
    ERROR = "ERROR",
    Active = "Active",
  }

  export enum LifeCycleState {
    Publish = "Publish",
  }
}

export enum UsagePlan {
  Gold = "Gold",
  Silver = "Silver",
  Bronze = "Bronze",
  Unlimited = "Unlimited",
}
export enum EndpointAccessibility {
  Public = "Public",
  Project = "Project",
  Organization = "Organization",
}

export enum CustomDomainType {
  Api,
  WebApp,
  DevPortal,
}

export enum ApiVisibility {
  Public = "Public",
  Private = "Private",
  Restricted = "Restricted",
}
export enum BuildPacks {
  Ballerina = "Ballerina",
  Go = "Go",
  MI = "MI",
  WEBAPP = "WebApp",
  DOCKER = "Docker"
}

export enum SecurityScheme {
  ApiKey,
  OAuth2,
}
