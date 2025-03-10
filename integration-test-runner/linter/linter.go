package linter

import (
	"encoding/json"
	"fmt"
	"os"
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

func ValidateSequenceOrder(steps []Step) error {
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

func main() {
	data, err := os.ReadFile("data.json")
	if err != nil {
		fmt.Println("Error reading file:", err)
		return
	}

	// Parse JSON into struct
	var steps []Step
	err = json.Unmarshal(data, &steps)
	if err != nil {
		fmt.Println("Error parsing JSON:", err)
		return
	}

	// Validate the sequence order
	if err := ValidateSequenceOrder(steps); err != nil {
		fmt.Println(err)
		return
	}

	// If validation passes, print steps
	for _, step := range steps {
		fmt.Printf("Executing: Sequence %d, Function: %s\n", step.Sequence, step.Function)
	}
}
