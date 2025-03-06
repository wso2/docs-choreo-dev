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
	"choreo-integration-test-runner/helper/name"
	"choreo-integration-test-runner/model/request"
	res "choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"
	"choreo-integration-test-runner/template"
	"context"
	"errors"

	"github.com/go-resty/resty/v2"
)

type CreateComponent struct {
	project     *res.CreateProject
	placeholder string
	orgId       int
}

func (c *CreateComponent) CreateComponent(ctx context.Context, sequence int, params map[string]string) {
	ctx, ok := validator.PreExecutionSetup(ctx, sequence, params, c)

	if !ok {
		return
	}

	client := choreo.GetClient(ctx)

	state := choreo.GetState(ctx)
	funcState := state.FunctionStates[sequence]

	c.createComponent(client, state, &funcState, params)
}

func (c *CreateComponent) createComponent(client *resty.Client, state *runner.State, funcState *[]runner.Run, params map[string]string) {
	name := name.NewName("autotest")

	request := request.CreateComponent{
		Name:              name.NameWithSeparators(),
		Description:       params["description"],
		OrgId:             c.orgId,
		DisplayType:       params["displayType"],
		ProjectId:         c.project.Project.Id,
		Accessibility:     params["accessibility"],
		SrcGitRepoURL:     params["srcGitRepoURL"],
		RepositorySubPath: params["repositorySubPath"],
		RepositoryBranch:  params["repositoryBranch"],
		IsPublicRepo:      params["isPublicRepo"] == "true",
	}

	response, rawResponse, status := api.CreateComponent(client, request)

	if status.IsFailed() {
		*funcState = append(*funcState, runner.Run{
			RunState: runner.Failed,
			Reason:   status.GetMessage(),
		})
	} else {
		if validator.ValidateResponse(state, funcState, rawResponse, c) {
			state.ComponentResponseHolder[c.placeholder] = *response
			state.ComponentRequestHolder[c.placeholder] = request
		}
	}
}

func (c *CreateComponent) Sanitize(state *runner.State, params map[string]string) error {
	mandatoryFields := []string{"placeholder", "description", "orgName", "displayType", "project", "accessibility", "srcGitRepoURL", "repositorySubPath", "repositoryBranch", "isPublicRepo"}
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
	c.orgId = state.OrgHolder.OrgId

	return nil
}

func (c *CreateComponent) ReadExpectedResponse() (*bytes.Buffer, error) {
	expected := struct {
		OrgId     int
		ProjectId string
		Handler   string
	}{
		OrgId:     c.orgId,
		ProjectId: c.project.Project.Id,
		Handler:   c.placeholder,
	}

	return template.PopulateResponseTemplate("createComponent", expected)
}
