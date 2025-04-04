package linter

import (
	"encoding/json"
	"fmt"
)

// Step struct to match JSON structure
type Step struct {
	Sequence int               `json:"sequence"`
	Function string            `json:"function"`
	Params   map[string]string `json:"params,omitempty"`
}

type actionData struct {
	Placeholder string
	Sequence    int
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
	for _, step := range steps {
		placeholder := ""
		if val, exists := step.Params["placeholder"]; exists {
			placeholder = val
		}

		specExecutionTree[step.Function] = append(specExecutionTree[step.Function], actionData{
			Sequence:    step.Sequence,
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

	for _, step := range steps {
		// Collect dependencies from map
		if requiredDeps, exists := dependencies[step.Function]; exists {
			for _, dep := range requiredDeps {
				depAction, found := specExecutionTree[dep]
				if !found {
					return fmt.Errorf("ERROR: Cannot execute %s (Sequence %d) because dependency %s is missing or comes later", step.Function, step.Sequence, dep)
				}
				for _, action := range depAction {
					if action.Sequence >= step.Sequence {
						return fmt.Errorf("ERROR: Cannot execute %s (Sequence %d) before %s (Sequence %d)", step.Function, step.Sequence, dep, action.Sequence)
					}
				}
			}
		}

	}

	return nil
}
