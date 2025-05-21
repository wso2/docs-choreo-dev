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

type promoteEndpoints struct {
	params *PromoteEndpointsParams
	state  *runner.SpecState
}

type PromoteEndpointsParams struct {
	CompDetails     *response.GetComponentDetails
	SrcEnvironment  *response.Environment
	DestEnvironment *response.Environment
}

func PromoteEndpoints(state *runner.SpecState, params *PromoteEndpointsParams) *promoteEndpoints {
	return &promoteEndpoints{
		params: params,
		state:  state,
	}
}

func (p *promoteEndpoints) Execute(client *resty.Client) (unit.UnitComplete, error) {
	latestApiVersion, err := p.params.CompDetails.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	srcEnvReleaseId, err := getEnvironmentReleaseId(latestApiVersion, p.params.SrcEnvironment)

	if err != nil {
		return false, err
	}

	req := request.PromoteEndpoints{
		ComponentId:         p.params.CompDetails.Component.Id,
		VersionId:           latestApiVersion.Id,
		SourceReleaseId:     srcEnvReleaseId,
		TargetEnvironmentId: p.params.DestEnvironment.Id,
	}

	_, err = api.PromoteEndpoints(client, req)

	if err != nil {
		return false, err
	}

	return true, nil
}

func (p *promoteEndpoints) Name() string {
	return "promoteEndpoints"
}

func (p *promoteEndpoints) WaitTill() int64 {
	return 0
}
