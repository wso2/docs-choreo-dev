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
	"choreo-integration-test-runner/helper/name"
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/runner"

	"choreo-integration-test-runner/template"
	"errors"

	"github.com/go-resty/resty/v2"
)

type responseGenerator struct {
	orgId     int
	projectId string
	handler   string
}

type component struct {
	sequence          int
	params            map[string]string
	componentDetails  componentDetails
	responseGenarator responseGenerator
}

func CreateComponent(sequence int, params map[string]string) *component {
	return &component{
		sequence:         sequence,
		params:           params,
		componentDetails: componentDetails{},
	}
}

func (c *component) GetParams() map[string]string {
	return c.params
}
func (c *component) GetSequence() int {
	return c.sequence
}

func (c *component) Execute(client *resty.Client, state *runner.SpecState, actionState *runner.ActionState, params map[string]string) runner.ExecutionResult {
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

	name := name.NewName("autotest")

	request := request.CreateComponent{
		Name:              name.NameWithSeparators(),
		Description:       params["description"],
		OrgId:             state.GetOrgId(),
		OrgHandler:        state.GetOrgHandler(),
		DisplayName:       name.NameWithSeparators(),
		DisplayType:       params["displayType"],
		ProjectId:         project.Project.Id,
		Accessibility:     params["accessibility"],
		SrcGitRepoUrl:     params["srcGitRepoURL"],
		RepositorySubPath: params["repositorySubPath"],
		RepositoryBranch:  params["repositoryBranch"],
		IsPublicRepo:      params["isPublicRepo"] == "true",
	}

	response, status := api.CreateComponent(client, request)

	if status.IsFailed() {
		actionState.Runs = append(actionState.Runs, runner.Run{
			RunState: runner.Failed,
			Reason:   status.GetMessage(),
		})
	} else {
		placeholder := params["placeholder"]
		rawResponse := status.GetRawResponse()

		state.SetComponentResponse(placeholder, *response)
		state.SetComponentRequest(placeholder, request)

		c.responseGenarator = responseGenerator{
			orgId:     state.GetOrgId(),
			projectId: project.Project.Id,
			handler:   response.Component.Handler,
		}

		return runner.ExecutionResult{
			Response:           rawResponse,
			IsValidateResponse: true,
		}
	}

	return runner.ExecutionResult{
		Response:           nil,
		IsValidateResponse: false,
	}
}

func (c *component) SanitizeParams(params map[string]string) error {
	mandatoryFields := []string{"placeholder", "displayType", "project",
		"accessibility", "srcGitRepoURL", "repositorySubPath", "repositoryBranch", "isPublicRepo"}

	for _, field := range mandatoryFields {
		if _, ok := params[field]; !ok {
			return errors.New(field + " is required")
		}
	}

	return nil
}

func (c *component) GetSubAction() runner.SubAction {
	return &c.componentDetails
}

func (c *component) GetResponseGenerator() runner.ResponseGenerator {
	return &c.responseGenarator
}

func (c *responseGenerator) GenExpectedResponse() (*bytes.Buffer, error) {
	expected := struct {
		OrgId     int
		ProjectId string
		Handler   string
	}{
		OrgId:     c.orgId,
		ProjectId: c.projectId,
		Handler:   c.handler,
	}

	return template.PopulateResponseTemplate("createComponent", expected)
}
