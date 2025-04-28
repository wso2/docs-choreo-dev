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

package interpreter

import (
	"choreo-integration-test-runner/choreo/action/component"
	"choreo-integration-test-runner/choreo/action/project"
	"choreo-integration-test-runner/linter"
	"choreo-integration-test-runner/runner"
	"fmt"
	"os"
	"strings"

	"gopkg.in/yaml.v3"
)

type ActionMapper struct {
	Function string            `yaml:"action"`
	Params   map[string]string `yaml:"params,omitempty"`
}

type SpecMapper []ActionMapper

var actionMapper = map[string]func() runner.Action{
	"CreateProject":    project.CreateProject,
	"CreateComponent":  component.CreateComponent,
	"WaitForBuild":     component.WaitForBuild,
	"GetEnvironments":  component.GetEnvironments,
	"DeployComponent":  component.DeployComponent,
	"PromoteComponent": component.PromoteComponent,
	"InvokeDeployment": component.InvokeDeployment,
	"InvokePromotion":  component.InvokePromotion,
}

func Process(specPath string) ([]*runner.Spec, error) {
	entries, err := os.ReadDir(specPath)

	if err != nil {
		return nil, err
	}

	specs := make([]*runner.Spec, 0, len(entries))

	for _, entry := range entries {
		if entry.IsDir() {
			subspecs, err := Process(specPath + "/" + entry.Name())

			if err != nil {
				return nil, err
			}

			specs = append(specs, subspecs...)
		} else {
			content, err := os.ReadFile(specPath + "/" + entry.Name())

			if err != nil {
				return nil, err
			}

			l := linter.NewLinter()

			err = l.ValidateSequenceOrder(string(content))

			if err != nil {
				return nil, err
			}

			spec, err := loadSpec(entry.Name(), content)

			if err != nil {
				return nil, err
			}

			specs = append(specs, spec)
		}
	}

	return specs, nil
}

func loadSpec(name string, spec []byte) (*runner.Spec, error) {
	mapping := SpecMapper{}
	err := yaml.Unmarshal(spec, &mapping)

	actions := make([]runner.Action, 0, len(mapping))

	if err != nil {
		return nil, err
	}

	for _, a := range mapping {
		actionCtr, exists := actionMapper[a.Function]

		if !exists {
			return nil, fmt.Errorf("action %s not found", a.Function)
		}

		action := actionCtr()
		err = action.SetParams(a.Params, action.MandatoryFields())

		if err != nil {
			return nil, err
		}

		actions = append(actions, action)
	}

	withoutExtension, found := strings.CutSuffix(name, ".yaml")

	if !found {
		return nil, fmt.Errorf("failed to cut .yaml suffix for %s", name)
	}

	return runner.NewSpec(withoutExtension, actions), nil
}
