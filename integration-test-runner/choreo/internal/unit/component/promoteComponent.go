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
	"choreo-integration-test-runner/helper/stop"
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"
	"fmt"

	"github.com/go-resty/resty/v2"
)

type promoteComponent struct {
	params *PromoteComponentParams
	state  *runner.SpecState
}

type PromoteComponentParams struct {
	CompDetails     *response.GetComponentDetails
	SrcEnvironment  *response.Environment
	DestEnvironment *response.Environment
	CommitHistory   *response.GetCommitHistory
}

func PromoteComponent(state *runner.SpecState, params *PromoteComponentParams) *promoteComponent {
	return &promoteComponent{
		params: params,
		state:  state,
	}
}

func (g *promoteComponent) Execute(client *resty.Client) (unit.UnitComplete, error) {
	latestApiVersion, err := g.params.CompDetails.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	srcEnvReleaseId, err := getEnvironmentReleaseId(latestApiVersion, g.params.SrcEnvironment)

	if err != nil {
		return false, err
	}

	promoteReq := request.PromoteComponent{
		ComponentId:         g.params.CompDetails.Component.Id,
		ApiVersionId:        latestApiVersion.Id,
		SourceReleaseId:     srcEnvReleaseId,
		TargetEnvironmentId: g.params.DestEnvironment.Id,
	}

	promoteRes := stop.HandleValueWithError(api.PromoteComponent(client, promoteReq))

	if promoteRes.Promote == "success" {
		return true, nil
	}

	return false, fmt.Errorf("failed to promote component: %s, received: %s", g.params.CompDetails.Component.Name, promoteRes.Promote)
}

func (g *promoteComponent) Name() string {
	return "promoteComponent"
}

func (g *promoteComponent) WaitTill() int64 {
	return 0
}
