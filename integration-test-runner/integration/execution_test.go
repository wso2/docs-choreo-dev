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
	"choreo-integration-test-runner/choreo/action/component"
	"choreo-integration-test-runner/choreo/action/project"
	"choreo-integration-test-runner/helper/appstate"
	"choreo-integration-test-runner/helper/stop"
	"choreo-integration-test-runner/runner"
	"choreo-integration-test-runner/template"
	"fmt"
	"os"
	"testing"
)

func TestRunMultipleSpecs(t *testing.T) {
	runIntegration := os.Getenv("RUNNER_INTEGRATION")
	if runIntegration == "" {
		t.Skip("set RUNNER_INTEGRATION to run this test")
	}

	if err := choreo.LoadConfigs("dev-env-config.yaml"); err != nil {
		t.Fatalf("Config loading failed: %v", err)
	}

	if err := template.LoadTemplates(); err != nil {
		t.Fatalf("Template loading failed: %v", err)
	}

	noOfSpecs := 1

	specs := make([]*runner.Spec, noOfSpecs)

	for i := 0; i < noOfSpecs; i++ {
		specs[i] = specBuilder(fmt.Sprintf("BasicTest%d", i), "testproject", "servicecomp")
	}

	sch, err := runner.NewScheduler(specs, 10)

	if err != nil {
		t.Fatalf("Failed to create scheduler: %v", err)
	}

	sch.Run()

	failedRunTimeData := sch.FailedSpecs()

	if len(failedRunTimeData) != 0 {
		for _, data := range failedRunTimeData {
			state := appstate.GetState(data.Ctx).(*runner.SpecState)

			failedSeq := state.NextSequenceIndex()

			failedAction := state.GetActionState(failedSeq)

			run, err := failedAction.GetLatestRun()

			if err != nil {
				t.Fatalf("Failed to get latest run: %v", err)
			}

			if run.RunState != runner.Failed {
				t.Fatalf("Expected Failed, got %v", run.RunState)
			}

			t.Fatalf("Failed reason for action %d: %s", failedSeq, run.Reason)
		}
	}

	runtimeData := sch.CompletedSpecs()

	if len(runtimeData) != noOfSpecs {
		t.Fatalf("Expected %d completed spec, got %d", noOfSpecs, len(runtimeData))
	}

	for i := 0; i < noOfSpecs; i++ {
		state := appstate.GetState(runtimeData[i].Ctx).(*runner.SpecState)

		createProjectAction := state.GetActionState(1)

		run, err := createProjectAction.GetLatestRun()

		if err != nil || run.RunState != runner.Success {
			t.Fatalf("Expected Success, got %v", run.RunState)
		}

		createComponentAction := state.GetActionState(2)

		run, err = createComponentAction.GetLatestRun()
		if err != nil || run.RunState != runner.Success {
			t.Fatalf("Expected Success, got %v", run.RunState)
		}

		_, err = state.GetComponentDetailsResponse("servicecomp")

		if err != nil {
			t.Fatalf("Expected component with placeholder servicecomp to exist")
		}
	}
}

func specBuilder(name, projectPlaceholder, componentPlaceholder string) *runner.Spec {
	actions := make([]runner.Action, 0)

	proj := project.CreateProject()
	proj.SetParams(map[string]string{
		"placeholder": projectPlaceholder,
		"description": "description",
		"region":      "US",
	}, proj.MandatoryFields())

	actions = append(actions, proj)

	createComp := component.CreateComponent()
	createComp.SetParams(map[string]string{
		"srcGitRepoURL":     "https://github.com/wso2/choreo-samples",
		"repositoryBranch":  "main",
		"repositorySubPath": "greeting-service",
		"displayType":       "ballerinaService",
		"buildPack":         "Ballerina",
		"accessibility":     "external",
		"isPublicRepo":      "true",
		"project":           projectPlaceholder,
		"placeholder":       componentPlaceholder,
	}, createComp.MandatoryFields())

	actions = append(actions, createComp)

	waitBuild := component.WaitForBuild()
	stop.HandleError(waitBuild.SetParams(map[string]string{
		"component": componentPlaceholder,
	}, waitBuild.MandatoryFields()))

	actions = append(actions, waitBuild)

	getEnvs := component.GetEnvironments()
	stop.HandleError(getEnvs.SetParams(map[string]string{
		"project": projectPlaceholder,
	}, getEnvs.MandatoryFields()))

	actions = append(actions, getEnvs)

	deployComp := component.DeployComponent()
	stop.HandleError(deployComp.SetParams(map[string]string{
		"component": componentPlaceholder,
	}, deployComp.MandatoryFields()))

	actions = append(actions, deployComp)

	promoteComp := component.PromoteComponent()
	stop.HandleError(promoteComp.SetParams(map[string]string{
		"component": componentPlaceholder,
	}, promoteComp.MandatoryFields()))

	actions = append(actions, promoteComp)

	invokeDeployment := component.InvokeDeployment()
	stop.HandleError(invokeDeployment.SetParams(map[string]string{
		"component":   componentPlaceholder,
		"method":      "GET",
		"resource":    "/",
		"queryParams": "name=Hello",
		"statusCode":  "200",
		"response":    "{\n  \"from\": \"Choreo\",\n  \"to\": \"Hello\",\n  \"message\": \"Welcome to Choreo!\"\n}",
	}, invokeDeployment.MandatoryFields()))

	actions = append(actions, invokeDeployment)

	invokePromotion := component.InvokePromotion()
	stop.HandleError(invokePromotion.SetParams(map[string]string{
		"component":   componentPlaceholder,
		"method":      "GET",
		"resource":    "/",
		"queryParams": "name=Hello",
		"statusCode":  "200",
		"response":    "{\n  \"from\": \"Choreo\",\n  \"to\": \"Hello\",\n  \"message\": \"Welcome to Choreo!\"\n}",
	}, invokePromotion.MandatoryFields()))

	actions = append(actions, invokePromotion)

	return runner.NewSpec(name, actions)
}
