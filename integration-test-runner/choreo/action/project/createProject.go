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

package project

import (
	"choreo-integration-test-runner/choreo/internal/base/action"
	"choreo-integration-test-runner/choreo/internal/unit/project"
	"choreo-integration-test-runner/runner"
)

type createProject struct {
	action.BaseAction
}

func CreateProject() runner.Action {
	return &createProject{}
}

func (w *createProject) RunMode() runner.RunMode {
	return runner.ALL
}

func (w *createProject) MandatoryFields() []string {
	return []string{"description", "region", "placeholder"}
}

func (w *createProject) Init(state *runner.SpecState) error {
	createProject := project.CreateProject(state, &project.CreateProjectParams{
		Description: w.ParamValue("description"),
		Region:      w.ParamValue("region"),
		Placeholder: w.ParamValue("placeholder"),
	})

	w.Append(createProject)

	return nil
}
