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

type getEndpoints struct {
	params *GetEndpointsParams
	state  *runner.SpecState
}

type GetEndpointsParams struct {
	CompDetails *response.GetComponentDetails
	Environment *response.Environment
}

func GetEndpoints(state *runner.SpecState, params *GetEndpointsParams) *getEndpoints {
	return &getEndpoints{
		params: params,
		state:  state,
	}
}

func (g *getEndpoints) Execute(client *resty.Client) (unit.UnitComplete, error) {
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

	getReq := request.GetEndpoints{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
		ReleaseId:   releaseId,
	}

	getRes, err := api.GetEndpoints(client, getReq)

	if err != nil {
		return false, err
	}

	if len(getRes.Endpoints) == 0 {
		return false, fmt.Errorf("failed to get endpoints for component %s", g.params.CompDetails.Component.Name)
	}

	g.state.SetEndpoints(runner.EndpointKey{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
		ReleaseId:   releaseId,
	}, *getRes)

	return true, nil
}

func (g *getEndpoints) Name() string {
	return "getEndpoints"
}

func (g *getEndpoints) WaitTill() int64 {
	return 0
}
