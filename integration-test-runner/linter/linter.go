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

package linter

import (
	"fmt"

	"gopkg.in/yaml.v3"
)

// action struct to match yaml spec
type action struct {
	Name   string            `yaml:"action"`
	Params map[string]string `yaml:"params"`
}

type spec struct {
	Shared  bool     `yaml:"shared"`
	Actions []action `yaml:"actions"`
}

type relationData struct {
	name         string
	isParent     bool
	parentKey    string
	parent       *relationData
	dependencies []*relationData
}

type actionOrder struct {
	act   *action
	index int
}

// Unique constants for action names
const (
	createProjectName       = "CreateProject"
	createComponentName     = "CreateComponent"
	createByocComponentName = "CreateByocComponent"
	getEnvironmentsName     = "GetEnvironments"
	waitForBuildName        = "WaitForBuild"
	deployComponentName     = "DeployComponent"
	promoteComponentName    = "PromoteComponent"
	invokeDeploymentName    = "InvokeDeployment"
	invokePromotionName     = "InvokePromotion"
)

/*
Actions can be related via parent-child or as dependency relationships. This dictates the order that actions
are declared in the spec. The relationship rules are as follows:

 1. A child action can have only one parent action.
 2. A parent action can have multiple child actions.
 3. An action may have a parent and children at the same time.
 4. A child action can have multiple actions as dependencies, which are not its parent.
 5. A parent must be declared before its child in the spec.
 6. A dependency must be declared before the action that depends on it.
*/
var createProject = relationData{name: createProjectName, isParent: true}
var createComponent = relationData{name: createComponentName, isParent: true, parentKey: "project", parent: &createProject}
var createByocComponent = relationData{name: createByocComponentName, isParent: true, parentKey: "project", parent: &createProject}
var getEnvironments = relationData{name: getEnvironmentsName, parentKey: "project", parent: &createProject}
var waitForBuild = relationData{name: waitForBuildName, parentKey: "component", parent: &createComponent}
var deployComponent = relationData{name: deployComponentName, parentKey: "component", parent: &createComponent, dependencies: []*relationData{&waitForBuild, &getEnvironments}}
var promoteComponent = relationData{name: promoteComponentName, parentKey: "component", parent: &createComponent, dependencies: []*relationData{&deployComponent}}
var invokeDeployment = relationData{name: invokeDeploymentName, parentKey: "component", parent: &createComponent, dependencies: []*relationData{&deployComponent}}
var invokePromotion = relationData{name: invokePromotionName, parentKey: "component", parent: &createComponent, dependencies: []*relationData{&promoteComponent}}

var relationships = map[string]relationData{
	createProjectName:       createProject,
	createComponentName:     createComponent,
	createByocComponentName: createByocComponent,
	getEnvironmentsName:     getEnvironments,
	waitForBuildName:        waitForBuild,
	deployComponentName:     deployComponent,
	promoteComponentName:    promoteComponent,
	invokeDeploymentName:    invokeDeployment,
	invokePromotionName:     invokePromotion,
}

type linter struct {
	parents   map[string]interface{}
	specOrder map[string][]*actionOrder
}

func NewLinter() *linter {
	return &linter{
		parents:   make(map[string]interface{}),
		specOrder: make(map[string][]*actionOrder),
	}
}

// ValidateSequenceOrder ensures steps execute in the correct order while tracking placeholders
func (l *linter) ValidateSequenceOrder(content []byte) error {
	var spec spec
	err := yaml.Unmarshal(content, &spec)
	if err != nil {
		return err
	}

	return l.validate(spec.Actions)
}

func (l *linter) validate(acts []action) error {
	err := l.buildSpecOrder(acts)

	if err != nil {
		return err
	}

	err = l.verifyOrder(acts)

	if err != nil {
		return err
	}

	return nil
}

func (l *linter) buildSpecOrder(acts []action) error {
	for index, act := range acts {
		rel, exists := relationships[act.Name]
		if !exists {
			return fmt.Errorf("action: %s is not recognized", act.Name)
		}

		if rel.isParent {
			keyValue, exists := act.Params["placeholder"]

			if !exists {
				return fmt.Errorf("action: %s is missing placeholder parameter", act.Name)
			}

			_, exists = l.parents[keyValue]

			if exists {
				return fmt.Errorf("parent with placeholder: %s already exists", keyValue)
			}

			l.parents[keyValue] = nil

			l.updateSpecOrder(keyValue, &act, index)
		}

		if rel.parentKey != "" {
			keyValue, exists := act.Params[rel.parentKey]

			if !exists {
				return fmt.Errorf("action %s is missing is missing its parent key parameter %s", act.Name, rel.parentKey)
			}

			l.updateSpecOrder(keyValue, &act, index)
		} else if !rel.isParent {
			return fmt.Errorf("child action %s is missing its parent key parameter", act.Name)
		}
	}

	return nil
}

func (l *linter) verifyOrder(acts []action) error {
	for index, act := range acts {
		rel := relationships[act.Name]

		if rel.parent != nil {
			parentkeyValue := act.Params[rel.parentKey]

			actsOrder := l.specOrder[parentkeyValue]

			for _, item := range actsOrder {
				if item.act.Name == rel.parent.name {
					if item.index >= index {
						return fmt.Errorf("parent %s is not placed before action %s", rel.parent.name, act.Name)
					}
				}
			}

			if rel.dependencies != nil {
				for _, dep := range rel.dependencies {
					if dep.parentKey == rel.parentKey { // Dependecy & action share same parent
						for _, item := range actsOrder {
							if dep.name == item.act.Name {
								if item.index >= index {
									return fmt.Errorf("dependency %s is not placed before action %s", dep.name, act.Name)
								}
							}
						}
					} else { // Dependency is not directly related to action
						return l.verifyUnrelatedDependecy(act, index, &rel, dep)
					}
				}
			}
		}

	}

	return nil
}

func (l *linter) verifyUnrelatedDependecy(act action, index int, rel *relationData, dep *relationData) error {
	switch dep.name {
	case getEnvironmentsName:
		if rel.name == deployComponentName {

			componentKeyValue := act.Params[rel.parentKey]

			compActsOrder := l.specOrder[componentKeyValue]

			for _, i := range compActsOrder {
				if i.act.Name == createComponentName || i.act.Name == createByocComponentName {
					projKeyVal := i.act.Params["project"]

					projActsOrder := l.specOrder[projKeyVal]

					for _, j := range projActsOrder {
						if j.act.Name == getEnvironmentsName {
							if j.index >= index {
								return fmt.Errorf("dependency %s is not placed before action %s", dep.name, act.Name)
							} else {
								return nil
							}
						}
					}
				}
			}

			return fmt.Errorf("action %s is missing dependency %s", act.Name, dep.name)
		} else {
			return fmt.Errorf("unhandled relationship %s depends on %s", rel.name, getEnvironmentsName)
		}
	default:
		return fmt.Errorf("action %s has unhandled dependency %s", act.Name, dep.name)
	}
}

func (l *linter) updateSpecOrder(parentKeyValue string, act *action, index int) {
	order, exists := l.specOrder[parentKeyValue]

	if !exists {
		order = make([]*actionOrder, 0)
	}
	order = append(order, &actionOrder{act: act, index: index})
	l.specOrder[parentKeyValue] = order
}
