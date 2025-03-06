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
}

type RunState int

const (
	Success RunState = iota
	Failed
	Skipped
)

type Run struct {
	RunState RunState
	Reason   string
}

type State struct {
	UnrecoverableError            error
	OrgHolder                     OrgHolder
	ProjectHolder                 map[string]response.CreateProject
	ComponentRequestHolder        map[string]request.CreateComponent
	ComponentResponseHolder       map[string]response.CreateComponent
	ComponentDetailResponseHolder map[string]response.GetComponentDetails
	FunctionStates                map[int][]Run
}

func NewState(orgHolder *OrgHolder, sequences []int) *State {
	state := &State{
		OrgHolder:                     *orgHolder,
		UnrecoverableError:            nil,
		ProjectHolder:                 make(map[string]response.CreateProject),
		ComponentRequestHolder:        make(map[string]request.CreateComponent),
		ComponentResponseHolder:       make(map[string]response.CreateComponent),
		ComponentDetailResponseHolder: make(map[string]response.GetComponentDetails),
		FunctionStates:                make(map[int][]Run),
	}

	for _, sequence := range sequences {
		state.FunctionStates[sequence] = make([]Run, 1)
	}

	return state
}
