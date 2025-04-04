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

package integration

import (
	"choreo-integration-test-runner/choreo"
	"choreo-integration-test-runner/choreo/component"
	"choreo-integration-test-runner/choreo/project"
	"choreo-integration-test-runner/helper/appstate"
	"choreo-integration-test-runner/runner"
	"choreo-integration-test-runner/template"
	"fmt"
	"os"
	"testing"
)

func TestCreateProject(t *testing.T) {
	runIntegration := os.Getenv("RUNNER_INTEGRATION")
	if runIntegration == "" {
		t.Skip("set RUNNER_INTEGRATION to run this test")
	}

	if err := choreo.LoadConfigs("dev-env-config.yaml"); err != nil {
		t.Errorf("Config loading failed: %v", err)
	}

	if err := template.LoadTemplates(); err != nil {
		t.Errorf("Template loading failed: %v", err)
	}

	noOfSpecs := 6

	specs := make([]*runner.Spec, noOfSpecs)

	for i := 0; i < noOfSpecs; i++ {
		specs[i] = specBuilder(fmt.Sprintf("BasicTest%d", i), "testproject", "servicecomp")
	}

	sch, err := runner.NewScheduler(specs, 10)

	if err != nil {
		t.Errorf("Failed to create scheduler: %v", err)
	}

	sch.Run()

	runtimeData := sch.CompletedSpecs()

	if len(runtimeData) != noOfSpecs {
		t.Errorf("Expected 1 completed spec, got %d", len(runtimeData))
	}

	for i := 0; i < noOfSpecs; i++ {
		state := appstate.GetState(runtimeData[i].Ctx).(*runner.SpecState)

		createProjectAction, ok := state.GetActionState(1)

		if !ok || createProjectAction.Runs[0].RunState != runner.Success {
			t.Errorf("Expected Success, got %v", createProjectAction.Runs[0].RunState)
		}

		createComponentAction, ok := state.GetActionState(2)

		if !ok || createComponentAction.Runs[0].RunState != runner.Success {
			t.Errorf("Expected Success, got %v", createComponentAction.Runs[0].RunState)
		}

		_, ok = state.GetComponentDetailsResponse("servicecomp")

		if !ok {
			t.Errorf("Expected component with placeholder servicecomp to exist")
		}
	}
}

func specBuilder(name, projectPlaceholder, componentPlaceholder string) *runner.Spec {
	actions := make([]runner.Action, 0, 3)

	actions = append(actions, project.CreateProject(1, map[string]string{
		"placeholder": projectPlaceholder,
		"description": "description",
		"region":      "US",
	}))

	actions = append(actions, component.CreateComponent(2, map[string]string{
		"srcGitRepoURL":     "https://github.com/choreo-test-apps/byor-service-app1",
		"repositoryBranch":  "main",
		"repositorySubPath": "",
		"displayType":       "ballerinaService",
		"buildPack":         "Ballerina",
		"accessibility":     "external",
		"isPublicRepo":      "true",
		"project":           projectPlaceholder,
		"placeholder":       componentPlaceholder,
	}))

	actions = append(actions, component.WaitForBuild(3, map[string]string{
		"component": componentPlaceholder,
	}))

	return runner.NewSpec(name, actions)
}
