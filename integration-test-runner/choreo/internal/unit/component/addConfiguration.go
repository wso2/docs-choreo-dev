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

type addConfiguration struct {
	params *AddConfigurationParams
	state  *runner.SpecState
}

type AddConfigurationParams struct {
	Project       *response.CreateProject
	CompDetails   *response.GetComponentDetails
	Environment   *response.Environment
	CommitHistory *response.GetCommitHistory
}

func AddConfiguration(state *runner.SpecState, params *AddConfigurationParams) *addConfiguration {
	return &addConfiguration{
		params: params,
		state:  state,
	}
}

func (g *addConfiguration) Execute(client *resty.Client) (unit.UnitComplete, error) {
	if g.params.CompDetails.Component.DisplayType != "ballerinaService" {
		return true, nil
	}

	latestApiVersion, err := g.params.CompDetails.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	commit, err := g.params.CommitHistory.GetLatestCommit()

	if err != nil {
		return false, err
	}

	configReq := request.AddConfiguration{
		ComponentId:     g.params.CompDetails.Component.Id,
		EnvId:           g.params.Environment.Id,
		LatestVersionId: latestApiVersion.Id,
		OrgHandle:       g.state.GetOrgHandler(),
		ProjectId:       g.params.Project.Project.Id,
		ModuleName:      g.params.CompDetails.Component.Name,
		CommitHash:      commit.SHA,
	}

	err = api.AddConfiguration(client, configReq)

	if err != nil {
		return false, err
	}

	return true, nil
}

func (g *addConfiguration) Name() string {
	return "addConfiguration"
}

func (g *addConfiguration) WaitTill() int64 {
	return 0
}
