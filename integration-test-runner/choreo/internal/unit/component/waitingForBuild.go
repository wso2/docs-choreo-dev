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
	"choreo-integration-test-runner/runner"
	"fmt"
	"time"

	"github.com/go-resty/resty/v2"
)

type waitForBuild struct {
	params *WaitForBuildParams
	state  *runner.SpecState
}

type WaitForBuildParams struct {
	Placeholder string
}

func WaitForBuild(state *runner.SpecState, params *WaitForBuildParams) *waitForBuild {
	return &waitForBuild{
		params: params,
		state:  state,
	}
}

func (w *waitForBuild) Execute(client *resty.Client) (unit.UnitComplete, error) {
	detailsRes, err := w.state.GetComponentDetailsResponse(w.params.Placeholder)

	if err != nil {
		return false, err
	}

	latestApiVersion, err := detailsRes.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	buildStatusReq := request.GetDeploymentStatusByVersion{
		ComponentId:     detailsRes.Component.Id,
		LatestVersionId: latestApiVersion.Id,
	}

	buidStatusRes, err := api.GetDeploymentStatusByVersion(client, buildStatusReq)

	if err != nil {
		return false, err
	}

	if len(buidStatusRes.DeploymentStatusByVersion) > 0 {
		deploymentStatus := buidStatusRes.DeploymentStatusByVersion[0]

		status := deploymentStatus.Status
		conclusion := deploymentStatus.ConclusionV2

		if status == "in_progress" || status == "queued" {
			return false, nil
		} else if status == "completed" && conclusion == "success" {
			return true, nil
		} else {
			return false, fmt.Errorf("build failed, status: %s, conclusion: %s", status, conclusion)
		}
	} else {
		return false, nil
	}
}

func (w *waitForBuild) Name() string {
	return "WaitForBuild"
}

func (w *waitForBuild) WaitTill() int64 {
	return time.Now().Unix() + wait.BuildIntervalSeconds
}
