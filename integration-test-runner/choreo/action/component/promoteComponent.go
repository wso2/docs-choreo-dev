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
)

type promoteComponent struct {
	action.BaseAction
}

func PromoteComponent() runner.Action {
	return &promoteComponent{}
}

func (w *promoteComponent) RunMode() runner.RunMode {
	return runner.CHECKPOINTS
}

func (w *promoteComponent) MandatoryFields() []string {
	return []string{"component"}
}

func (w *promoteComponent) Init(state *runner.SpecState) error {

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

	commitHistory, err := state.CommitHistory(w.ParamValue("component"))

	if err != nil {
		return err
	}

	srcEnvIndex := 0
	destEnvIndex := 1

	for destEnvIndex < len(envsRes.Environments) {

		w.Append(component.PromoteEndpoints(state, &component.PromoteEndpointsParams{
			CompDetails:     &detailsRes,
			SrcEnvironment:  &envsRes.Environments[srcEnvIndex],
			DestEnvironment: &envsRes.Environments[destEnvIndex],
		}))

		w.Append(component.GetEndpoints(state, &component.GetEndpointsParams{
			CompDetails: &detailsRes,
			Environment: &envsRes.Environments[destEnvIndex],
		}))

		w.Append(component.UpdateEndpoint(state, &component.UpdateEndpointParams{
			CompDetails: &detailsRes,
			Environment: &envsRes.Environments[destEnvIndex],
		}))

		w.Append(component.AddConfiguration(state, &component.AddConfigurationParams{
			Project:       &project,
			CompDetails:   &detailsRes,
			Environment:   &envsRes.Environments[destEnvIndex],
			CommitHistory: &commitHistory,
		}))

		w.Append(component.PromoteComponent(state, &component.PromoteComponentParams{
			CompDetails:     &detailsRes,
			SrcEnvironment:  &envsRes.Environments[srcEnvIndex],
			DestEnvironment: &envsRes.Environments[destEnvIndex],
		}))

		w.Append(component.WaitForDeployment(state, &component.WaitForDeploymentParams{
			CompDetails: &detailsRes,
			Environment: &envsRes.Environments[destEnvIndex],
		}))

		w.Append(component.WaitForEndpoint(state, &component.WaitForEndpointsParams{
			CompDetails: &detailsRes,
			Environment: &envsRes.Environments[destEnvIndex],
		}))

		srcEnvIndex++
		destEnvIndex++
	}

	return nil
}

func (w *promoteComponent) Name() string {
	return "PromoteComponent"
}
