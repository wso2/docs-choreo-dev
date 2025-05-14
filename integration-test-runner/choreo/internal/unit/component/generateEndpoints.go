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
	"fmt"

	"github.com/go-resty/resty/v2"
)

type generateEndpoints struct {
	params *GenerateEndpointsParams
	state  *runner.SpecState
}

type GenerateEndpointsParams struct {
	CompDetails   *response.GetComponentDetails
	Environment   *response.Environment
	CommitHistory *response.GetCommitHistory
}

func GenerateEndpoints(state *runner.SpecState, params *GenerateEndpointsParams) *generateEndpoints {
	return &generateEndpoints{
		params: params,
		state:  state,
	}
}

func (g *generateEndpoints) Execute(client *resty.Client) (unit.UnitComplete, error) {
	if g.params.CompDetails.Component.DisplayType != "ballerinaService" {
		return true, nil
	}

	latestApiVersion, err := g.params.CompDetails.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	releaseId, err := getEnvironmentReleaseId(latestApiVersion, g.params.Environment)

	if err != nil {
		return false, err
	}

	commit, err := g.params.CommitHistory.GetLatestCommit()

	if err != nil {
		return false, err
	}

	genReq := request.GenerateEndpoints{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
		ReleaseId:   releaseId,
		CommitHash:  commit.SHA,
	}

	genEndpointsRes, err := api.GenerateEndpoints(client, genReq)

	if err != nil {
		return false, err
	}

	if len(genEndpointsRes.GenerateComponentEndpoints) == 0 {
		return false, fmt.Errorf("failed to generate endpoints for component %s", g.params.CompDetails.Component.Name)
	}

	return true, nil
}

func (g *generateEndpoints) Name() string {
	return "generateEndpoints"
}

func (g *generateEndpoints) WaitTill() int64 {
	return 0
}

func getEnvironmentReleaseId(apiVersion *response.ApiVersion, env *response.Environment) (string, error) {
	for _, appVersion := range apiVersion.AppEnvVersions {
		if appVersion.EnvironmentId == env.Id {
			return appVersion.ReleaseId, nil
		}
	}

	return "", fmt.Errorf("matching environment with Id : %s could not be found to retrieve releaseId", env.Id)
}
