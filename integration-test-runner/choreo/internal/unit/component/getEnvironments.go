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
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"

	"github.com/go-resty/resty/v2"
)

type getEnvironments struct {
	params *GetEnvironmentsParams
	state  *runner.SpecState
}

type GetEnvironmentsParams struct {
	Project *response.CreateProject
}

func GetEnvironments(state *runner.SpecState, params *GetEnvironmentsParams) *getEnvironments {
	return &getEnvironments{
		params: params,
		state:  state,
	}
}

func (g *getEnvironments) Execute(client *resty.Client) (unit.UnitComplete, error) {
	request := request.GetDeploymentEnvironments{
		OrgUuid:   g.state.GetOrgUuid(),
		ProjectId: g.params.Project.Project.Id,
	}

	deploymentEnvs, err := api.GetDeploymentEnvironments(client, request)

	if err != nil {
		return false, err
	}

	g.state.SetDeploymentEnvironments(runner.EnvKey{
		OrgUuid:   g.state.GetOrgUuid(),
		ProjectId: g.params.Project.Project.Id,
	}, *deploymentEnvs)

	return true, nil
}

func (g *getEnvironments) Name() string {
	return "getEnvironments"
}

func (g *getEnvironments) WaitTill() int64 {
	return 0
}
