package linter

import (
	"encoding/json"
	"fmt"
)

// Step struct to match JSON structure
type Step struct {
	Sequence int    `json:"sequence"`
	Function string `json:"function"`
}

var dependencies = map[string][]string{
	"CreateComponent":    {"CreateProject"},
	"GetEnvironments":    {"CreateProject"},
	"WaitForBuild":       {"CreateComponent"},
	"DeployComponent":    {"WaitForBuild", "GetEnvironments"},
	"Promote":            {"DeployComponent"},
	"InvokeDevEndpoint":  {"DeployComponent"},
	"InvokeProdEndpoint": {"Promote"},
}

func ValidateSequenceOrder(content string) error {
	var steps []Step
	err := json.Unmarshal([]byte(content), &steps)
	if err != nil {
		return fmt.Errorf("ERROR: JSON Unmarshal failed: %v", err)
	}

	seen := make(map[string]bool)

	for _, step := range steps {
		// Check if this step has dependencies
		if requiredDeps, exists := dependencies[step.Function]; exists {
			for _, dep := range requiredDeps {
				if !seen[dep] {
					return fmt.Errorf("ERROR: Cannot execute %s (Sequence %d) because dependency %s is missing or comes later!", step.Function, step.Sequence, dep)
				}
			}
		}

		// Mark the function as executed
		seen[step.Function] = true
	}
	return nil
}
