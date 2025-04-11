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

type FieldValidator interface {
	MandatoryFields() []string
}

type createComponent struct {
	action.BaseAction
}

func CreateComponent() *createComponent {
	return &createComponent{}

}

func (c *createComponent) RunMode() runner.RunMode {
	return runner.ALL
}

func (c *createComponent) MandatoryFields() []string {
	return []string{"placeholder", "displayType", "project",
		"accessibility", "srcGitRepoURL", "repositorySubPath", "repositoryBranch", "isPublicRepo"}
}

func (c *createComponent) Init(state *runner.SpecState) error {
	project, err := state.GetProject(c.ParamValue("project"))

	if err != nil {
		return err
	}

	createComp := component.CreateComponent(state, &component.CreateComponentParams{
		ProjectRes:        &project,
		Placeholder:       c.ParamValue("placeholder"),
		Description:       c.ParamValue("description"),
		DisplayType:       c.ParamValue("displayType"),
		OrgId:             state.GetOrgId(),
		Accessibility:     c.ParamValue("accessibility"),
		SrcGitRepoURL:     c.ParamValue("srcGitRepoURL"),
		RepositorySubPath: c.ParamValue("repositorySubPath"),
		RepositoryBranch:  c.ParamValue("repositoryBranch"),
		IsPublicRepo:      c.ParamValue("isPublicRepo") == "true",
	})

	getCompDetails := component.GetComponentDetails(state, &component.GetComponentDetailsParams{
		ProjectRes:  &project,
		Placeholder: c.ParamValue("placeholder"),
	})

	getCommitHistory := component.GetCommitHistoryByBranch(state, &component.GetCommitHistoryByBranchParams{
		Placeholder: c.ParamValue("placeholder"),
	})

	c.Append(createComp)
	c.Append(getCompDetails)
	c.Append(getCommitHistory)

	return nil
}
