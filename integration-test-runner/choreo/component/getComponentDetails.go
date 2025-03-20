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

package component

import (
	"bytes"
	"choreo-integration-test-runner/choreo/internal/api"
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"
	"choreo-integration-test-runner/template"

	"github.com/go-resty/resty/v2"
)

type detailResponseGenerator struct {
	name        string
	orgId       int
	orgHandler  string
	projectId   string
	displayType string
}

type componentDetails struct {
	responseGenarator detailResponseGenerator
}

func (c *componentDetails) Execute(client *resty.Client, state *runner.SpecState, actionState *runner.ActionState, params map[string]string) runner.ExecutionResult {
	projectPlaceholder := params["project"]
	project, ok := state.GetProject(projectPlaceholder)
	if !ok {
		actionState.Runs = append(actionState.Runs, runner.Run{
			RunState: runner.Failed,
			Reason:   "Project with placeholder `" + projectPlaceholder + "` not found",
		})
		return runner.ExecutionResult{
			Response:           nil,
			IsValidateResponse: false,
		}
	}

	placeholder := params["placeholder"]

	createCompRes, ok := state.GetComponentResponse(placeholder)

	if !ok {
		actionState.Runs = append(actionState.Runs, runner.Run{
			RunState: runner.Failed,
			Reason:   "Component with placeholder `" + placeholder + "` not found",
		})
		return runner.ExecutionResult{
			Response:           nil,
			IsValidateResponse: false,
		}
	}

	request := request.GetComponentDetails{
		ProjectId:        project.Project.Id,
		ComponentHandler: createCompRes.Component.Handler,
	}

	response, status := api.GetComponentDetails(client, request)

	if status.IsFailed() {
		actionState.Runs = append(actionState.Runs, runner.Run{
			RunState: runner.Failed,
			Reason:   status.GetMessage(),
		})
	} else {
		state.SetComponentDetailsResponse(placeholder, *response)

		createCompReq, _ := state.GetComponentRequest(placeholder)
		c.responseGenarator = detailResponseGenerator{
			name:        createCompReq.Name,
			orgId:       project.Project.OrgId,
			orgHandler:  state.GetOrgHandler(),
			projectId:   project.Project.Id,
			displayType: createCompReq.DisplayType,
		}

		return runner.ExecutionResult{
			Response:           status.GetRawResponse(),
			IsValidateResponse: true,
		}
	}

	return runner.ExecutionResult{
		Response:           nil,
		IsValidateResponse: false,
	}
}

func (c *componentDetails) GetSubAction() runner.SubAction {
	return nil
}

func (c *componentDetails) GetResponseGenerator() runner.ResponseGenerator {
	return &c.responseGenarator
}

func (c *detailResponseGenerator) GenExpectedResponse() (*bytes.Buffer, error) {
	expected := response.Component{
		Name:        c.name,
		OrgId:       c.orgId,
		OrgHandler:  c.orgHandler,
		ProjectId:   c.projectId,
		DisplayType: c.displayType,
	}

	return template.PopulateResponseTemplate("getComponentDetails", expected)
}
