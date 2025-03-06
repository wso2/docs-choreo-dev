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
	"choreo-integration-test-runner/choreo"
	"choreo-integration-test-runner/choreo/internal/api/config"
	"choreo-integration-test-runner/runner"
	"choreo-integration-test-runner/template"
	"os"
	"testing"
)

func TestCreateProject(t *testing.T) {
	runIntegration := os.Getenv("RUNNER_INTEGRATION")
	if runIntegration == "" {
		t.Skip("set RUNNER_INTEGRATION to run this test")
	}

	if err := config.LoadConfigs("dev-env-config.yaml"); err != nil {
		t.Errorf("Config loading failed: %v", err)
	}

	if err := template.LoadTemplates(); err != nil {
		t.Errorf("Template loading failed: %v", err)
	}

	ctx, err := choreo.InitCtx("CreateProject", []int{1})

	if err != nil {
		t.Errorf("Context initialization failed: %v", err)
	}

	params := map[string]string{
		"placeholder": "test1",
		"description": "description",
		"region":      "US",
	}

	proj := Project{}

	proj.CreateProject(ctx, 1, params)

	state := choreo.GetState(ctx)

	if state.FunctionStates[1][0].RunState != runner.Success {
		t.Errorf("Expected Success, got %v", state.FunctionStates[1][0].RunState)
	}

	_, ok := state.ProjectHolder["test1"]

	if !ok {
		t.Errorf("Expected project with placeholder test1 to exist")
	}
}
