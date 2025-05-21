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
	"choreo-integration-test-runner/choreo/internal/unit"
	"choreo-integration-test-runner/helper/name"
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/runner"

	"github.com/go-resty/resty/v2"
)

type createProject struct {
	params *CreateProjectParams
	state  *runner.SpecState
}

type CreateProjectParams struct {
	Description string
	Region      string
	Placeholder string
}

func CreateProject(state *runner.SpecState, params *CreateProjectParams) *createProject {
	return &createProject{
		params: params,
		state:  state,
	}
}

func (g *createProject) Execute(client *resty.Client) (unit.UnitComplete, error) {
	name := name.NewName("autotest")

	req := request.CreateProject{
		Name:           name.NameWithSeparators(),
		Description:    g.params.Description,
		ProjectHandler: name.Name(),
		OrgId:          g.state.GetOrgId(),
		OrgHandler:     g.state.GetOrgHandler(),
		Region:         g.params.Region,
	}

	resp, err := api.CreateProject(client, req)

	if err != nil {
		return false, err
	}

	g.state.SetProject(g.params.Placeholder, *resp)

	return true, nil
}

func (g *createProject) Name() string {
	return "createProject"
}

func (g *createProject) WaitTill() int64 {
	return 0
}
