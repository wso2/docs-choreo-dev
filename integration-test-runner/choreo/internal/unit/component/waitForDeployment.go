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

type waitForDeployment struct {
	params *WaitForDeploymentParams
	state  *runner.SpecState
}

type WaitForDeploymentParams struct {
	CompDetails *response.GetComponentDetails
	Environment *response.Environment
}

func WaitForDeployment(state *runner.SpecState, params *WaitForDeploymentParams) *waitForDeployment {
	return &waitForDeployment{
		params: params,
		state:  state,
	}
}

func (g *waitForDeployment) Execute(client *resty.Client) (unit.UnitComplete, error) {
	latestApiVersion, err := g.params.CompDetails.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	request := request.GetComponentDeployment{
		OrgHandler:  g.state.GetOrgHandler(),
		OrgUuid:     g.state.GetOrgUuid(),
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
		EnvId:       g.params.Environment.Id,
	}

	deploymentRes, err := api.GetComponentDeployment(client, request)

	if err != nil {
		return false, err
	}

	err = detectErrors(deploymentRes, latestApiVersion.Id, g.params.Environment.Id)

	if err != nil {
		return false, err
	}

	if deploymentRes.ComponentDeployment.DeploymentStatus == "ACTIVE" &&
		deploymentRes.ComponentDeployment.DeploymentStatusV2 == "ACTIVE" {
		g.state.SetDeploymentResponse(g.params.Environment.Id, *deploymentRes)
		return true, nil
	} else {
		return false, nil
	}
}

func (g *waitForDeployment) Name() string {
	return "waitForDeployment"
}

func (g *waitForDeployment) WaitTill() int64 {
	return time.Now().Unix() + wait.DeployIntervalSeconds
}

func detectErrors(response *response.GetComponentDeployment, versionId string, envId string) error {
	if response.ComponentDeployment.VersionID != versionId {
		return errors.New("version Id mismatch")
	}

	if response.ComponentDeployment.EnvironmentID != envId {
		return errors.New("environment Id mismatch")
	}

	if response.ComponentDeployment.DeploymentStatus == "ERROR" ||
		response.ComponentDeployment.DeploymentStatusV2 == "ERROR" {
		return errors.New("deployment failed")
	}

	return nil
}
