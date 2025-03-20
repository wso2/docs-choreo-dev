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

package project

import (
	"choreo-integration-test-runner/choreo/internal/api"
	"choreo-integration-test-runner/helper/name"
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/runner"
	"errors"

	"github.com/go-resty/resty/v2"
)

type project struct {
	sequence int
	params   map[string]string
}

func CreateProject(sequence int, params map[string]string) *project {
	return &project{
		sequence: sequence,
		params:   params,
	}
}

func (p *project) GetParams() map[string]string {
	return p.params
}

func (p *project) GetSequence() int {
	return p.sequence
}

func (p *project) Execute(client *resty.Client, state *runner.SpecState, actionState *runner.ActionState, params map[string]string) runner.ExecutionResult {
	orgId := state.GetOrgId()
	orgHandler := state.GetOrgHandler()

	name := name.NewName("autotest")

	req := request.CreateProject{
		Name:           name.NameWithSeparators(),
		Description:    params["description"],
		ProjectHandler: name.Name(),
		OrgId:          orgId,
		OrgHandler:     orgHandler,
		Region:         params["region"],
	}

	resp, status := api.CreateProject(client, req)

	if status.IsFailed() {
		actionState.Runs = append(actionState.Runs, runner.Run{
			RunState: runner.Failed,
			Reason:   status.GetMessage(),
		})
	} else {
		placeholder := params["placeholder"]

		state.SetProject(placeholder, *resp)
		actionState.Runs = append(actionState.Runs, runner.Run{
			RunState: runner.Success,
		})
	}

	return runner.ExecutionResult{
		Response:           nil,
		IsValidateResponse: false,
	}
}

func (p *project) SanitizeParams(params map[string]string) error {
	mandatoryFields := []string{"description", "region", "placeholder"}
	for _, field := range mandatoryFields {
		if _, ok := params[field]; !ok {
			return errors.New(field + " is required")
		}
	}

	return nil
}

func (p *project) GetSubAction() runner.SubAction {
	return nil
}

func (p *project) GetResponseGenerator() runner.ResponseGenerator {
	return nil
}
