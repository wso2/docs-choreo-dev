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
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"
	"errors"
	"fmt"
	"time"

	"github.com/go-resty/resty/v2"
)

const (
	waitIntervalSeconds = 60 * 1
)

type build struct {
	sequence int
	params   map[string]string
}

func WaitForBuild(sequence int, params map[string]string) *build {
	return &build{
		sequence: sequence,
		params:   params,
	}
}

func (w *build) GetParams() map[string]string {
	return w.params
}

func (w *build) GetSequence() int {
	return w.sequence
}

func (w *build) Execute(client *resty.Client, state *runner.SpecState, actionState *runner.ActionState, params map[string]string) runner.ExecutionResult {
	componentPlaceholder := params["component"]
	component, _ := state.GetComponentDetailsResponse(componentPlaceholder)

	var latestApiVersion response.APIVersion
	var latesFound bool

	for _, apiVersion := range component.Component.APIVersions {
		if apiVersion.Latest {
			latestApiVersion = apiVersion
			latesFound = true
			break
		}
	}

	if !latesFound {
		actionState.Runs = append(actionState.Runs, runner.Run{
			RunState: runner.Failed,
			Reason:   "Latest API version not found",
		})
		return runner.ExecutionResult{
			Response:           nil,
			IsValidateResponse: false,
		}
	}

	request := request.GetDeploymentStatusByVersion{
		ComponentId:     component.Component.Id,
		LatestVersionId: latestApiVersion.Id,
	}

	response, status := api.GetDeploymentStatusByVersion(client, request)

	result := runner.ExecutionResult{
		Response:           nil,
		IsValidateResponse: false,
	}

	if status.IsFailed() {
		actionState.Runs = append(actionState.Runs, runner.Run{
			RunState: runner.Failed,
			Reason:   status.GetMessage(),
		})
	} else {
		if len(response.DeploymentStatusByVersion) > 0 {
			deploymentStatus := response.DeploymentStatusByVersion[0]

			status := deploymentStatus.Status
			conclusion := deploymentStatus.ConclusionV2

			if status == "in_progress" || status == "queued" {
				actionState.Runs = append(actionState.Runs, runner.Run{
					RunState: runner.Progressing,
				})
				result.IsWaiting = true
				state.SetWaitTill(time.Now().Unix() + waitIntervalSeconds)
			} else if status == "completed" && conclusion == "success" {
				actionState.Runs = append(actionState.Runs, runner.Run{
					RunState: runner.Success,
				})
			} else {
				actionState.Runs = append(actionState.Runs, runner.Run{
					RunState: runner.Failed,
					Reason:   fmt.Sprintf("build failed, status: %s, conclusion: %s", status, conclusion),
				})
			}
		} else {
			actionState.Runs = append(actionState.Runs, runner.Run{
				RunState: runner.Progressing,
			})
			result.IsWaiting = true
			state.SetWaitTill(time.Now().Unix() + waitIntervalSeconds)
		}
	}

	return result
}

func (w *build) SanitizeParams(params map[string]string) error {
	mandatoryFields := []string{"component"}
	for _, field := range mandatoryFields {
		if _, ok := params[field]; !ok {
			return errors.New(field + " is a mandatory field")
		}
	}

	return nil
}

func (w *build) GetSubAction() runner.SubAction {
	return nil
}

func (w *build) GetResponseGenerator() runner.ResponseGenerator {
	return nil
}
