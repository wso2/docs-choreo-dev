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

type getEnvironments struct {
	action.BaseAction
}

func GetEnvironments() runner.Action {
	return &getEnvironments{}
}

func (w *getEnvironments) RunMode() runner.RunMode {
	return runner.ALL
}

func (w *getEnvironments) MandatoryFields() []string {
	return []string{"project"}
}

func (w *getEnvironments) Init(state *runner.SpecState) error {
	project, err := state.GetProject(w.ParamValue("project"))

	if err != nil {
		return err
	}

	getEnvironments := component.GetEnvironments(state, &component.GetEnvironmentsParams{
		Project: &project,
	})

	w.Append(getEnvironments)

	return nil
}
