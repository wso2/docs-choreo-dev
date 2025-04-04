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

type SpecRunResult int

const (
	Pending SpecRunResult = iota
	Complete
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

type Run struct {
	RunState RunState
	Reason   string
}

type ActionState struct {
	Runs []Run
}

type SpecState struct {
	unrecoverableError  error
	orgHolder           OrgHolder
	nextSequenceIndex   int
	totalSequences      int
	waitTill            int64
	runResult           SpecRunResult
	projects            map[string]response.CreateProject
	componentReq        map[string]request.CreateComponent
	componentRes        map[string]response.CreateComponent
	componentDetailsRes map[string]response.GetComponentDetails
	environments        map[EnvKey]response.GetDeploymentEnvironments
	actionStates        map[int]ActionState
}

func NewState(orgHolder *OrgHolder, sequences []int) *SpecState {
	state := &SpecState{
		orgHolder:           *orgHolder,
		unrecoverableError:  nil,
		totalSequences:      len(sequences),
		projects:            make(map[string]response.CreateProject),
		componentReq:        make(map[string]request.CreateComponent),
		componentRes:        make(map[string]response.CreateComponent),
		componentDetailsRes: make(map[string]response.GetComponentDetails),
		environments:        make(map[EnvKey]response.GetDeploymentEnvironments),
		actionStates:        make(map[int]ActionState),
	}

	for _, sequence := range sequences {
		state.actionStates[sequence] = ActionState{Runs: make([]Run, 1)}
	}

	return state
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

func (s *SpecState) GetProject(placeholder string) (response.CreateProject, bool) {
	project, ok := s.projects[placeholder]
	return project, ok
}

func (s *SpecState) GetComponentRequest(placeholder string) (request.CreateComponent, bool) {
	component, ok := s.componentReq[placeholder]
	return component, ok
}

func (s *SpecState) GetComponentResponse(placeholder string) (response.CreateComponent, bool) {
	component, ok := s.componentRes[placeholder]
	return component, ok
}

func (s *SpecState) GetComponentDetailsResponse(placeholder string) (response.GetComponentDetails, bool) {
	component, ok := s.componentDetailsRes[placeholder]
	return component, ok
}

func (s *SpecState) GetDeploymentEnvironments(envKey EnvKey) (response.GetDeploymentEnvironments, bool) {
	env, ok := s.environments[envKey]
	return env, ok
}

func (s *SpecState) SetProject(placeholder string, project response.CreateProject) {
	s.projects[placeholder] = project
}

func (s *SpecState) SetComponentRequest(placeholder string, component request.CreateComponent) {
	s.componentReq[placeholder] = component
}

func (s *SpecState) SetComponentResponse(placeholder string, component response.CreateComponent) {
	s.componentRes[placeholder] = component
}

func (s *SpecState) SetComponentDetailsResponse(placeholder string, component response.GetComponentDetails) {
	s.componentDetailsRes[placeholder] = component
}

func (s *SpecState) SetDeploymentEnvironments(envKey EnvKey, env response.GetDeploymentEnvironments) {
	s.environments[envKey] = env
}

func (s *SpecState) GetActionState(sequence int) (*ActionState, bool) {
	actionState, ok := s.actionStates[sequence]
	return &actionState, ok
}

func (s *SpecState) SetActionState(sequence int, actionState ActionState) {
	s.actionStates[sequence] = actionState
}

func (s *SpecState) WaitTill() int64 {
	return s.waitTill
}

func (s *SpecState) SetWaitTill(waitTill int64) {
	s.waitTill = waitTill
}
