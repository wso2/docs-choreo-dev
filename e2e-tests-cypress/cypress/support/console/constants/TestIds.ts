/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

import { EndpointAccessibility, Enums } from "../../commons/enums";

export namespace TestIds {
  export const userProfile = '[data-testid="header-user-profile-menu"]';
  export const backdropLoader = '[data-testid="backdrop-loader"]';
  export const progressBar = '[role="progressbar"]';
  export const componentLoader = '[id="circular-loader"]';
  export const projectName = '[data-cyid="project-name"]';
  export const projectDescription = '[data-cyid="project-description"]';
  export const multiRepository = '[data-testid="Multi Repository-radio-card"]';
  export const createProject =
    '[data-cyid="create-project-stepper-submit-button"]';
  export const backToProjectList =
    '[data-cyid="sample-creation-dialog-closeBtn-button"]';
  export const projectPicker = '[data-testid="project-picker"]';
  export const createNew = '[data-cyid="btn-create-new"]';
  export const projectCard = '[data-cyid="create-project-card"]';
  export const searchIcon = '[data-cyid="search-icon-icon-button"]';
  export const projectSearch =
    '[data-cyid="page-action-auto-forcused-search-search-field"]';
  export const componentSearch =
    '[data-cyid="tab-action-auto-forcused-search-search-field"]';
  export const listing = '[data-cyid="listing"]';
  export const componentFilter =
    '[data-cyid="project-components-multi-select"]';
  export const componentTable = '[data-cyid="component-table"]';
  export const next = '[data-cyid="btn-next-button"]';
  export const buildCard = '[data-cyid="default-build-card"]';
  export const devEnvCard = '[data-cyid="env-baseDevelopment-env-card"]';
  export const prodEnvCard = '[data-cyid="env-baseProduction-env-card"]';
  export const deploymentStatus = '[data-cyid="deployment-status"]';
  export const deploymentStatusChip = '[data-cyid="deployment-status-chip"]';
  export const availableEndpoints = '[data-testid="Endpoints-env-artifact"]';
  export const viewArtifact = '[data-cyid="btn-view-artifact-icon-button"]';
  export const endpointCard = '[data-cyid="endponint-selector-card-card"]';
  export const selectVersion = '[data-cyid="deployment-track-picker-chip"]';
  export const selectBranch = '[data-cyid="deployment-track-picker"]';
  export const deploySplitToggle =
    '[data-cyid="direct-deploy-option-split-toggle-button-button"]';
  export const configureDeploy = '[data-cyid="configure-&-deploy-option"]';
  export const executeDeploy =
    '[data-cyid="direct-deploy-option-split-group-button-button"]';
  export const endpointVisibility = (visibility: EndpointAccessibility) =>
    `[data-testid="${visibility}-visibility-option"]`;
  export const endpointSubmit = '[data-cyid="endpoint-submit-btn-button"]';
  export const stop = '[data-testid="btn-stop"]';
  export const reDeploy = '[data-cyid="btn-redeploy-button"]';
  export const notificationBanner =
    '[data-testid="notification-with-icon-and-button"]';
  export const noEndpointNotification =
    '[data-testid="no-endpoints-notification"]';
  export const endpointStatus = '[data-cyid="Endpoints-status-chip"]';
  export const commitHistory = '[data-cyid="commit-history-detail-box"]';
  export const retry = '[data-testid="retry-btn"]';
  export const refresh = '[data-cyid="refresh-button-button"]';
  export const notDeployed = '[data-cyid="card-body-not-deployed"]';
  export const deploymentFetchError = '[data-testid="deployment-fetch-error"]';
  export const promote = '[data-cyid="btn-promote-button"]';
  export const createVersion = '[data-cyid="create-version-button"]';
  export const createProxyVersion = '[data-cyid="btn-create-version-button"]';
  export const createProxyVersionDialog =
    '[data-cyid="create-version-create-button"]';
  export const dialog = '[role="dialog"]';
  export const versionName = '[data-cyid="text-field-new-version"]';
  export const createDeploymentTrack =
    '[data-testid="create-deployment-track-create"]';
  export const publishLifecycle = '[data-testid="Publish-lc-btn"]';
  export const blockLifecycle = '[data-testid="Block-lc-btn"]';
  export const prereleaseLifecycle =
    '[data-testid="Deploy as a Prototype-lc-btn"]';
  export const demoteLifecycle = '[data-testid="Demote to Created-lc-btn"]';
  export const deprecateLifecycle = '[data-testid="Deprecate-lc-btn"]';
  export const usagePlanSave =
    '[data-cyid="usage-plans-primary-button-button"]';
  export const editSettings = '[data-cyid="btn-edit-settings-button"]';
  export const corsConfig = '[data-testid="switch-cors-config"]';
  export const saveSettings = '[data-cyid="btn-save-settings-button"]';
  export const apply = '[data-cyid="apply-button"]';
  export const projectInsights = '[data-cyid="project-usage-insights-button"]';
  export const datePicker = '[data-testid="date-picker"]';
  export const build = '[data-cyid="build-button"]';
  export const tableTitle = '[data-cyid="table-title"]';
  export const createComponent = '[data-cyid="create-component-button"]';
  export const proxyBuildPack =
    '[data-cyid="component-template-httpProxyApi-card-action-area"]';
  export const oasUrlEntry = '[data-cyid="txt-oas-url"]';
  export const upload = '[data-cyid="btn-upload-button"]';
  export const filepathEntry = 'input[type="file"]';
  export const apiName = '[data-cyid="api-name"]';
  export const apiVersion = '[data-cyid="api-version"]';
  export const apiBasePath = '[data-cyid="api-basepath"]';
  export const apiEndpoint = '[data-cyid="api-endpoint"]';
  export const internalAccessMode = '[data-testid="internal-radio-card"]';
  export const createButton = '[data-cyid="btn-create-button"]';
  export const createTime = '[data-cyid="create-time"]';
  export const skipSource = '[data-cyid="btn-skip-src-button"]';
  export const operation = '[data-testid="operation"]';
  export const deleteIcon = '[data-cyid="delete-icon-button"]';
  export const deployProxySplitToggle =
    '[data-cyid="direct-deploy-option-proxy-split-toggle-button-button"]';
  export const executeDeployProxySplitToggle =
    '[data-cyid="direct-deploy-option-proxy-split-group-button-button"]';
  export const save = '[data-cyid="get-save-button-button"]';
  export const expand = '[data-cyid="expand-more"]';
  export const deleteAllOperations =
    '[data-testid="delete-all-operations-btn"]';
  export const undoDeleteAllOperations =
    '[data-testid="undo-delete-all-operations-btn"]';
  export const uriPatternEntry =
    '[data-cyid="get-operation-target-placeholder"]';
  export const add = '[data-testid="add-btn"]';
  export const versionPicker = '[data-cyid="version-picker"]';
  export const buildStatus = '[data-cyid="map-build-status"]';
  export const devPortalLink = '[data-cyid="dev-portal-button-button"]';
  export const dialogPrimaryAction =
    '[data-cyid="confirmation-dialog-primary-action-button"]';
  export const publishBtn = '[data-cyid="publish-btn-button"]';
  export const generateCredentials = '[data-testid="generate-creds-btn"]';
  export const removeCredentials = '[data-testid="remove-creds-btn"]';
  export const generateAccessToken =
    '[data-testid="generate-access-token-btn"]';
  export const getTestKey = '[data-testid="get-test-key-btn"]';
  export const accessToken = '[data-testid="accessTokenInput"]';
  export const apiNameDevPortal = '[data-testid="txt-api-name"]';
  export const addScopeBtn = '[data-testid="scope-add-icon-button"]';
  export const addNewScope = '[data-testid="scope-add-new-btn"]';
  export const scopeTextInput = '[data-testid="scope-text-input"]';
  export const selectAllScopes = '[data-testid="scope-select-all-btn"]';
  export const applyScopesToAll = '[data-testid="scope-apply-to-all-btn"]';
  export const scopeSaveAndDeploy =
    '[data-cyid="scope-save-and-deploy-button"]';
  export const deleteAllScopes = '[data-testid="scope-delete-all-btn"]';
  export const permissionTag = (permission: string) =>
    `[data-cyid="${permission}-multiselect-tag"]`;
  export const scopeItem = (permission: string) =>
    `[data-testid="scope-item-${permission}"]`;
  export const scopeItemCheckBox = (permission: string) =>
    `[data-testid="scope-item-checkbox-${permission}"]`;
  export const applicationBar = '[data-testid="applications-appbar-btn"]';
  export const createApplication = '[data-testid="create-application-btn"]';
  export const applicationName = '[data-testid="app-name"]';
  export const applicationDescription =
    '[data-testid="application-description"]';
  export const createBtn = '[data-testid="create-button"]';
  export const applicationTokenType = '[data-testid="application-token-type"]';
  export const envCredentialsMenu = (env: Enums.Environment) =>
    `[data-testid="${env.toLowerCase()}-credentials-menu-item"]`;

  export const linkKeys = '[data-testid="link-production-keys"]';
  export const generateKey = '[data-testid="generate-oauth-key"]';
  export const consumerKey = "#consumer-key-text";
  export const subscriptions = '[data-testid="subscriptions"]';
  export const createSubscription = '[data-testid="create-subscription-btn"]';
  export const addApiSubscription = (apiName: string) =>
    `[data-testid="add-api-${apiName}"]`;
  export const subscriptionClose =
    '[data-testid="subscription-dialog-close-btn"]';
  export const applicationList = (appName: string) =>
    `[data-testid="application-list-${appName}"]`;
  export const applicationEdit = '[data-testid="appliation-edit-btn"]';
  export const permissionsField = '[data-testid="autocomplete-textfield"]';
  export const apiBar = '[data-testid="apis-appbar-btn"]';
  export const apiSearch = "#outlined-search-bar-api-listing";
  export const apiCard = (apiName: string) =>
    `[data-testid="apiCard-${apiName}"`;
  export const apiOverviewDevPortal = '[data-testid="li-overview-item-link"]';
  export const applicationSelect = '[data-testid="application-selector"]';
  export const applicationSelectItem = (application: string) =>
    `[data-value="${application}"]`;
  export const value = (value: string) => `[value=${value}]`;
  export const search = '[data-testid="search-btn"]';
  export const searchAppText =
    '[data-testid="search-app"] [placeholder="Search"]';
  export const appDeleteBtn = (appName: string) =>
    `[data-testid="delete-btn-${appName}"]`;
  export const deleteDialogOk = '[data-testid="delete-dialog-ok-button"]';
  export const applicationListEdit = (appName: string) =>
    `[data-testid="edit-btn-${appName}"]`;
  export const configSubmit = '[data-cyid="btn-submit-configform"]';
  export const runNow = '[data-cyid="run-now-button"]';
  export const runNowNotification = '[data-testid="snackbar-notification"]';
  export const refreshTasks = '[data-cyid="refresh-tasks-button"]';
  export const executionCount = '[data-cyid="total-executions-count"]';
  export const link = '[data-cyid="btn-link-button"]';
  export const mountPath = '[data-cyid="mount-path"]';
  export const formConfigField =
    '[class="view-lines monaco-mouse-cursor-text"]';
  export const appUrl = '[data-cyid="app-url-link"]';
  export const orgAppSecurity =
    '[data-cyid="nav-link-application-security-link-tabs-link-tab"]';
  export const builtInIdpCard = '[data-cyid="choreo-built-in-idp-card"]';
  export const linkBtn = '[data-cyid="link-button"]';
  export const selectUserStoreFile =
    '[data-cyid="userstore-file-uploader-button-button"]';
  export const uploadUserStoreFile = '[data-cyid="upload-csv-button"]';
  export const choreoIdpEnvs = '[data-cyid="choreo-idp-environments"]';
  export const idpEnv = (env: Enums.Environment) =>
    `[data-cyid="choreo-idp-environments-${env.toLowerCase()}"]`;
  export const idpEnvUS = (env: Enums.Environment) =>
    `[data-cyid="choreo-idp-environments-${env.toLowerCase()}---us"]`;
  export const uploadUserStoreCard = '[data-cyid="upload-userstores-card"]';
  export const observabilityLogPanelEntry = '[data-testid="log-panel-entry"]';
  export const environmentPickerObsMetrics =
    '[data-cyid="environment-selector-select"]';
  export const deploymentHistory =
    '[data-cyid="deployment-history-btn-button"]';
  export const resourceTab = '[data-cyid="tab-resource-settings"]';
  export const envSelector = '[data-cyid="environment-selector"]';
  export const envSelectorItems = '[data-cyid="environment-selector"]>div>div';
  export const diagramLoader = '[data-testid="diagram-loader"]';
  export const envSelectorItemsObservability =
    '[id="environment-selector-label-popup"]';
  export const durationSelector =
    '[data-cyid="undefined-date-time-container-button-button"]';
  export const refreshLogs = '[data-cyid="refresh-logs-button-button"]';
  export const revision = '[testid="selected-revision-link"]';
  export const revisionHistory = '[data-testid="revision-history-header"]';
  export const revisionItem = '[data-cyid*="revision-list-item"]';
  export const security = `[data-testid="security"]`;
  export const project = '[data-cyid="project-picker-button"]';
  export const endpoint = '[data-cyid="text-field-endpoint"]';
  export const accessMode = '[data-testid="access-mode"]';
  export const warningBanner = '[data-testid="warning-banner"]';
  export const addConfig = '[data-cyid="add-new-button"]';
  export const addConfigKey = '[data-cyid="key-value-card-add-new-new-input-name"]';
  export const addConfigValue = '[data-cyid="key-value-card-add-new-new-input-value"]';
  export const configSave = '[data-cyid="key-value-save-button"]';
  export const keyValueCheckBox= '[data-cyid="key-value-card-add-new-secret-checkbox-check-box"]';
  export const nextButton = '[data-testid="btn-next"]';
  export const fileMount = '[data-cyid="file-mount-upload-button"]';
  export const byocPromote = '[data-cyid="promote-selector-default-configs"]';
  
}
