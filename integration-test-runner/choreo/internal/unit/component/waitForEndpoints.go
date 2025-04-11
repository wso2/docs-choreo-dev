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
	"choreo-integration-test-runner/choreo/wait"
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"
	"errors"
	"time"

	"github.com/go-resty/resty/v2"
)

type waitForEndpoints struct {
	params *WaitForEndpointsParams
	state  *runner.SpecState
}

type WaitForEndpointsParams struct {
	CompDetails *response.GetComponentDetails
	Environment *response.Environment
}

func WaitForEndpoint(state *runner.SpecState, params *WaitForEndpointsParams) *waitForEndpoints {
	return &waitForEndpoints{
		params: params,
		state:  state,
	}
}

func (g *waitForEndpoints) Execute(client *resty.Client) (unit.UnitComplete, error) {
	latestApiVersion, err := g.params.CompDetails.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	releaseId, err := getEnvironmentReleaseId(latestApiVersion, g.params.Environment)

	if err != nil {
		return false, err
	}

	request := request.GetEndpoints{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
		ReleaseId:   releaseId,
	}

	endpointsResponse, err := api.GetEndpoints(client, request)

	if err != nil {
		return false, err
	}

	if len(endpointsResponse.Endpoints) > 0 {
		for _, endpoint := range endpointsResponse.Endpoints {
			if endpoint.State == "Error" {
				return false, errors.New("endpoint creation failed")
			}

			if endpoint.State != "Active" {
				return false, nil
			}
		}
	} else {
		return false, nil
	}

	g.state.SetEndpoints(runner.EndpointKey{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
		ReleaseId:   releaseId,
	}, *endpointsResponse)

	return true, nil
}

func (g *waitForEndpoints) Name() string {
	return "waitForEndpoints"
}

func (g *waitForEndpoints) WaitTill() int64 {
	return time.Now().Unix() + wait.DeployIntervalSeconds
}
