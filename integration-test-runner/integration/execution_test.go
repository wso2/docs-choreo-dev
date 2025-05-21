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
	"choreo-integration-test-runner/helper/appstate"
	"choreo-integration-test-runner/interpreter"
	"choreo-integration-test-runner/runner"
	"choreo-integration-test-runner/template"
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

	noOfSpecs := 2

	specs, err := specReader("../specs")

	if err != nil {
		t.Fatalf("Failed to read specs: %v", err)
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

	runtimeData := sch.SuccessSpecs()

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

func specReader(path string) ([]*runner.Spec, error) {
	specs, err := interpreter.Process(path)

	if err != nil {
		return nil, err
	}

	return specs, nil
}
