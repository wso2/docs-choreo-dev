package linter

import (
	"encoding/json"
	"fmt"
)

// Step struct to match JSON structure
type Step struct {
	Function string            `json:"function"`
	Params   map[string]string `json:"params,omitempty"`
}

type actionData struct {
	Placeholder string
	Index       int
}

// Track order of action execution within the spec
var specExecutionTree = make(map[string][]actionData)

var dependencies = map[string][]string{
	"CreateComponent":    {"CreateProject"},
	"GetEnvironments":    {"CreateProject"},
	"WaitForBuild":       {"CreateComponent"},
	"DeployComponent":    {"WaitForBuild", "GetEnvironments"},
	"Promote":            {"DeployComponent"},
	"InvokeDevEndpoint":  {"DeployComponent"},
	"InvokeProdEndpoint": {"Promote"},
}

func buildSpecExecutionTree(steps []Step) {
	for index, step := range steps {
		placeholder := ""
		if val, exists := step.Params["placeholder"]; exists {
			placeholder = val
		}

		specExecutionTree[step.Function] = append(specExecutionTree[step.Function], actionData{
			Index:       index,
			Placeholder: placeholder,
		})
	}
}

// ValidateSequenceOrder ensures steps execute in the correct order while tracking placeholders
func ValidateSequenceOrder(content string) error {
	var steps []Step
	err := json.Unmarshal([]byte(content), &steps)
	if err != nil {
		return fmt.Errorf("ERROR: JSON Unmarshal failed: %v", err)
	}

	buildSpecExecutionTree(steps)

	for currentIndex, step := range steps {
		if requiredDeps, exists := dependencies[step.Function]; exists {
			for _, dep := range requiredDeps {
				depActions, found := specExecutionTree[dep]
				if !found {
					return fmt.Errorf("ERROR: Cannot execute %s at position %d because dependency %s is missing", step.Function, currentIndex+1, dep)
				}
				for _, action := range depActions {
					if action.Index >= currentIndex {
						return fmt.Errorf("ERROR: Cannot execute %s at position %d before %s at position %d", step.Function, currentIndex+1, dep, action.Index+1)
					}
				}
			}
		}
	}

	return nil
}
