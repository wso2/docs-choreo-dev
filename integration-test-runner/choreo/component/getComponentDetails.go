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
	"choreo-integration-test-runner/choreo"
	"choreo-integration-test-runner/choreo/internal/api"
	"choreo-integration-test-runner/choreo/internal/validator"
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"
	"choreo-integration-test-runner/template"
	"context"
	"errors"

	"github.com/go-resty/resty/v2"
)

type GetComponentDetails struct {
	project        *response.CreateProject
	createRequest  *request.CreateComponent
	createResponse *response.CreateComponent
	placeholder    string
	orgHolder      runner.OrgHolder
}

func (c *GetComponentDetails) GetComponentDetails(ctx context.Context, sequence int, params map[string]string) {
	ctx, ok := validator.PreExecutionSetup(ctx, sequence, params, c)

	if !ok {
		return
	}

	client := choreo.GetClient(ctx)

	state := choreo.GetState(ctx)
	funcState := state.FunctionStates[sequence]

	c.getComponentDetails(client, state, &funcState)
}

func (c *GetComponentDetails) getComponentDetails(client *resty.Client, state *runner.State, funcState *[]runner.Run) {

	request := request.GetComponentDetails{
		ProjectId:        c.project.Project.Id,
		ComponentHandler: c.createResponse.Component.Handler,
	}

	response, rawResponse, status := api.GetComponentDetails(client, request)

	if status.IsFailed() {
		*funcState = append(*funcState, runner.Run{
			RunState: runner.Failed,
			Reason:   status.GetMessage(),
		})
	} else {
		if validator.ValidateResponse(state, funcState, rawResponse, c) {
			state.ComponentDetailResponseHolder[c.placeholder] = *response
		}
	}
}

func (c *GetComponentDetails) Sanitize(state *runner.State, params map[string]string) error {
	mandatoryFields := []string{"placeholder", "project"}
	for _, field := range mandatoryFields {
		if _, ok := params[field]; !ok {
			return errors.New(field + " is required")
		}
	}

	projectPlaceholder := params["project"]
	project, ok := state.ProjectHolder[projectPlaceholder]
	if !ok {
		return errors.New("Project with placeholder `" + projectPlaceholder + "` not found")
	}

	c.project = &project
	c.placeholder = params["placeholder"]
	req := state.ComponentRequestHolder[c.placeholder]
	res := state.ComponentResponseHolder[c.placeholder]
	c.createRequest = &req
	c.createResponse = &res
	c.orgHolder = state.OrgHolder

	return nil
}

func (c *GetComponentDetails) ReadExpectedResponse() (*bytes.Buffer, error) {
	expected := response.Component{
		Name:        c.createRequest.Name,
		OrgId:       c.orgHolder.OrgId,
		OrgHandler:  c.orgHolder.OrgHandler,
		ProjectId:   c.project.Project.Id,
		DisplayType: c.createRequest.DisplayType,
	}

	return template.PopulateResponseTemplate("getComponentDetails", expected)
}
