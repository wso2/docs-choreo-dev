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
	"choreo-integration-test-runner/choreo/internal/api"
	"choreo-integration-test-runner/choreo/internal/unit"
	"choreo-integration-test-runner/helper/name"
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"

	"github.com/go-resty/resty/v2"
)

type createByocComponent struct {
	params CreateByocComponentParams
	state  *runner.SpecState
}

type CreateByocComponentParams struct {
	ProjectRes        *response.CreateProject
	Placeholder       string
	Description       string
	ComponentType     string
	OrgId             int
	Accessibility     string
	SrcGitRepoURL     string
	RepositorySubPath string
	RepositoryBranch  string
	IsPublicRepo      bool
	DockerContext     string
	DockerFilePath    string
}

func CreateByocComponent(state *runner.SpecState, params *CreateByocComponentParams) *createByocComponent {
	return &createByocComponent{
		params: *params,
		state:  state,
	}
}

func (c *createByocComponent) Execute(client *resty.Client) (unit.UnitComplete, error) {
	name := name.NewName("autotest")

	req := request.CreateByocComponent{
		BaseComponent: request.BaseComponent{
			Name:        name.NameWithSeparators(),
			Description: c.params.Description,
			OrgId:       c.state.GetOrgId(),
			OrgHandler:  c.state.GetOrgHandler(),
			DisplayName: name.NameWithSeparators(),
			ProjectId:   c.params.ProjectRes.Project.Id,
		},
		ComponentType:     c.params.ComponentType,
		Accessibility:     c.params.Accessibility,
		SrcGitRepoUrl:     c.params.SrcGitRepoURL,
		RepositorySubPath: c.params.RepositorySubPath,
		RepositoryBranch:  c.params.RepositoryBranch,
		IsPublicRepo:      c.params.IsPublicRepo,
		ByocConfig: request.ByocConfig{
			SrcGitRepoUrl:    c.params.SrcGitRepoURL,
			SrcGitRepoBranch: c.params.RepositoryBranch,
			DockerFilePath:   c.params.DockerFilePath,
			DockerContext:    c.params.DockerContext,
		},
	}

	expected := response.CreateByocComponent{
		BaseComponent: response.BaseComponent{
			OrgId:     c.state.GetOrgId(),
			ProjectId: c.params.ProjectRes.Project.Id,
		},
	}

	res, err := api.CreateByocComponent(client, req, &expected)

	if err != nil {
		return false, err
	}

	c.state.SetComponentResponse(c.params.Placeholder, res.BaseComponent)
	c.state.SetComponentRequest(c.params.Placeholder, req.BaseComponent)

	return true, nil
}

func (c *createByocComponent) Name() string {
	return "CreateByocComponent"
}

func (c *createByocComponent) WaitTill() int64 {
	return 0
}
