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
	"choreo-integration-test-runner/choreo/internal/base/action"
	"choreo-integration-test-runner/choreo/internal/unit/component"
	"choreo-integration-test-runner/runner"
	"strconv"
)

type invokeDeployment struct {
	action.BaseAction
}

func InvokeDeployment() *invokeDeployment {
	return &invokeDeployment{}
}

func (w *invokeDeployment) RunMode() runner.RunMode {
	return runner.CHECKPOINTS
}

func (w *invokeDeployment) MandatoryFields() []string {
	return []string{"component", "method", "statusCode"}
}

func (w *invokeDeployment) Init(state *runner.SpecState) error {
	detailsRes, err := state.GetComponentDetailsResponse(w.ParamValue("component"))

	if err != nil {
		return err
	}

	project, err := state.FindProjectById(detailsRes.Component.ProjectId)

	if err != nil {
		return err
	}

	envsRes, err := state.GetDeploymentEnvironments(runner.EnvKey{
		OrgUuid:   state.GetOrgUuid(),
		ProjectId: project.Project.Id,
	})

	if err != nil {
		return err
	}

	devEnv := envsRes.Environments[0]

	apiKey := component.GetApiKey(&component.GetApiKeyParam{
		CompDetails: &detailsRes,
		Environment: &devEnv,
	}, state)

	satusCode, err := strconv.Atoi(w.ParamValue("statusCode"))

	if err != nil {
		return err
	}

	invokeUrl := component.InvokeUrl(state, &component.InvokeUrlParams{
		CompDetails:        &detailsRes,
		Environment:        &devEnv,
		HttpMethod:         w.ParamValue("method"),
		ResourcePath:       w.ParamValue("resource"),
		QueryParams:        w.ParamValue("queryParams"),
		Request:            w.ParamValue("request"),
		ExpectedStatusCode: satusCode,
		ExpectedResponse:   w.ParamValue("response"),
	})

	w.Append(apiKey)
	w.Append(invokeUrl)

	return nil
}
