/*
 * Copyright © 2025 WSO2 LLC. (http://www.wso2.com).
 *
 * This software is the property of WSO2 LLC and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package runner

import (
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"errors"
)

type OrgHolder struct {
	OrgId      int
	OrgHandler string
	OrgUuid    string
}

type EnvKey struct {
	OrgUuid   string
	ProjectId string
}

type EndpointKey struct {
	ComponentId string
	VersionId   string
	ReleaseId   string
}

type BuildImagesKey struct {
	ComponentId string
	VersionId   string
}

type ApiKeyKey struct {
	ApiId   string
	OrgUuid string
	KeyType string
}

type SpecRunResult int

const (
	Pending SpecRunResult = iota
	Successful
	Running
	Waiting
	Error
)

type RunState int

const (
	Success RunState = iota
	Progressing
	Failed
	Skipped
)

type RunMode int

const (
	ALL RunMode = iota
	CHECKPOINTS
)

type Run struct {
	RunState RunState
	Reason   string
	WaitTill int64
}

type ActionState struct {
	runs []Run
}

func (a *ActionState) GetLatestRun() (Run, error) {
	if len(a.runs) == 0 {
		return Run{}, errors.New("no runs found")
	}
	return a.runs[len(a.runs)-1], nil
}

func (a *ActionState) StoreRun(run Run) {
	a.runs = append(a.runs, run)
}

type SpecState struct {
	unrecoverableError  error
	orgHolder           OrgHolder
	nextSequenceIndex   int
	waitTill            int64
	waitCount           int
	runResult           SpecRunResult
	projects            map[string]response.CreateProject
	componentReq        map[string]request.BaseComponent
	componentRes        map[string]response.BaseComponent
	commitHistory       map[string]response.GetCommitHistory
	componentDetailsRes map[string]response.GetComponentDetails
	environments        map[EnvKey]response.GetDeploymentEnvironments
	endpoints           map[EndpointKey]response.GetEndpoints
	buildImages         map[BuildImagesKey]response.GetBuildImages
	deploymentRes       map[string]response.GetComponentDeployment
	apiKeys             map[ApiKeyKey]response.GetApiKey
	actionStates        []ActionState
}

func NewState(orgHolder *OrgHolder, numberOfActions int) *SpecState {
	state := &SpecState{
		orgHolder:           *orgHolder,
		unrecoverableError:  nil,
		projects:            make(map[string]response.CreateProject),
		componentReq:        make(map[string]request.BaseComponent),
		componentRes:        make(map[string]response.BaseComponent),
		commitHistory:       make(map[string]response.GetCommitHistory),
		componentDetailsRes: make(map[string]response.GetComponentDetails),
		environments:        make(map[EnvKey]response.GetDeploymentEnvironments),
		endpoints:           make(map[EndpointKey]response.GetEndpoints),
		buildImages:         make(map[BuildImagesKey]response.GetBuildImages),
		deploymentRes:       make(map[string]response.GetComponentDeployment),
		apiKeys:             make(map[ApiKeyKey]response.GetApiKey),
		actionStates:        make([]ActionState, numberOfActions),
	}

	for i := 0; i < numberOfActions; i++ {
		state.actionStates[i] = ActionState{runs: make([]Run, 0, 1)}
	}

	return state
}

func (s *SpecState) NextSequenceIndex() int {
	return s.nextSequenceIndex
}

func (s *SpecState) NumberOfActions() int {
	return len(s.actionStates)
}

func (s *SpecState) SetUnrecoverableError(err error) {
	s.unrecoverableError = err
}

func (s *SpecState) GetUnrecoverableError() error {
	return s.unrecoverableError
}

func (s *SpecState) GetOrgId() int {
	return s.orgHolder.OrgId
}

func (s *SpecState) GetOrgHandler() string {
	return s.orgHolder.OrgHandler
}

func (s *SpecState) GetOrgUuid() string {
	return s.orgHolder.OrgUuid
}

func (s *SpecState) GetProject(placeholder string) (response.CreateProject, error) {
	project, ok := s.projects[placeholder]

	if !ok {
		return project, errors.New("project details not found for placeholder: " + placeholder)
	}

	return project, nil
}

func (s *SpecState) FindProjectById(projectId string) (response.CreateProject, error) {
	for _, project := range s.projects {
		if project.Project.Id == projectId {
			return project, nil
		}
	}
	return response.CreateProject{}, errors.New("project not found")
}

func (s *SpecState) GetComponentRequest(placeholder string) (request.BaseComponent, error) {
	component, ok := s.componentReq[placeholder]

	if !ok {
		return component, errors.New("create component request not found for placeholder: " + placeholder)
	}

	return component, nil
}

func (s *SpecState) GetComponentResponse(placeholder string) (response.BaseComponent, error) {
	component, ok := s.componentRes[placeholder]

	if !ok {
		return component, errors.New("create component response not found for placeholder: " + placeholder)
	}

	return component, nil
}

func (s *SpecState) GetComponentDetailsResponse(placeholder string) (response.GetComponentDetails, error) {
	component, ok := s.componentDetailsRes[placeholder]

	if !ok {
		return component, errors.New("component details not found for placeholder: " + placeholder)
	}

	return component, nil
}

func (s *SpecState) GetDeploymentEnvironments(envKey EnvKey) (response.GetDeploymentEnvironments, error) {
	env, ok := s.environments[envKey]

	if !ok {
		return env, errors.New("deployment environments not found")
	}

	return env, nil
}

func (s *SpecState) Endpoints(epKey EndpointKey) (response.GetEndpoints, error) {
	endpoint, ok := s.endpoints[epKey]
	if !ok {
		return endpoint, errors.New("endpoints not found for component: " + epKey.ComponentId)
	}
	return endpoint, nil
}

func (s *SpecState) BuildImages(buildImagesKey BuildImagesKey) (response.GetBuildImages, error) {
	buildImage, ok := s.buildImages[buildImagesKey]
	if !ok {
		return buildImage, errors.New("build images not found for component: " + buildImagesKey.ComponentId)
	}
	return buildImage, nil
}

func (s *SpecState) ApiKey(apiKeyKey ApiKeyKey) (response.GetApiKey, error) {
	apiKey, ok := s.apiKeys[apiKeyKey]
	if !ok {
		return apiKey, errors.New("api key not found for component: " + apiKeyKey.ApiId)
	}
	return apiKey, nil
}

func (s *SpecState) CommitHistory(placeholder string) (response.GetCommitHistory, error) {
	commitHistory, ok := s.commitHistory[placeholder]

	if !ok {
		return commitHistory, errors.New("commit history not found for placeholder: " + placeholder)
	}

	return commitHistory, nil
}

func (s *SpecState) DeploymentResponse(placeholder string) (response.GetComponentDeployment, error) {
	deploymentRes, ok := s.deploymentRes[placeholder]

	if !ok {
		return deploymentRes, errors.New("deployment response not found for placeholder: " + placeholder)
	}

	return deploymentRes, nil
}

func (s *SpecState) SetProject(placeholder string, project response.CreateProject) {
	s.projects[placeholder] = project
}

func (s *SpecState) SetComponentRequest(placeholder string, component request.BaseComponent) {
	s.componentReq[placeholder] = component
}

func (s *SpecState) SetComponentResponse(placeholder string, component response.BaseComponent) {
	s.componentRes[placeholder] = component
}

func (s *SpecState) SetComponentDetailsResponse(placeholder string, component response.GetComponentDetails) {
	s.componentDetailsRes[placeholder] = component
}

func (s *SpecState) SetDeploymentEnvironments(envKey EnvKey, env response.GetDeploymentEnvironments) {
	s.environments[envKey] = env
}

func (s *SpecState) SetEndpoints(epKey EndpointKey, endpoint response.GetEndpoints) {
	s.endpoints[epKey] = endpoint
}

func (s *SpecState) SetBuildImages(buildImagesKey BuildImagesKey, buildImage response.GetBuildImages) {
	s.buildImages[buildImagesKey] = buildImage
}

func (s *SpecState) SetApiKey(apiKeyKey ApiKeyKey, apiKey response.GetApiKey) {
	s.apiKeys[apiKeyKey] = apiKey
}

func (s *SpecState) SetCommitHistory(placeholder string, commitHistory response.GetCommitHistory) {
	s.commitHistory[placeholder] = commitHistory
}

func (s *SpecState) SetDeploymentResponse(envId string, deploymentRes response.GetComponentDeployment) {
	s.deploymentRes[envId] = deploymentRes
}

func (s *SpecState) GetActionState(index int) ActionState {
	return s.actionStates[index]
}

func (s *SpecState) SetActionState(sequence int, actionState ActionState) {
	s.actionStates[sequence] = actionState
}

func (s *SpecState) WaitTill() int64 {
	return s.waitTill
}

func (s *SpecState) SetWaitTill(waitTill int64) {
	s.waitTill = waitTill

	s.waitCount++
}

func (s *SpecState) ResetWaitTill() {
	s.waitTill = 0
}

func (s *SpecState) WaitCount() int {
	return s.waitCount
}
