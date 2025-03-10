package linter

import (
	"encoding/json"
	"testing"
)

// Test cases
func TestValidateSequenceOrder(t *testing.T) {
	tests := []struct {
		name          string
		steps         []Step
		expectSuccess bool
		expectError   string
	}{
		// Valid order: Should pass
		{
			name: "Valid sequence order",
			steps: []Step{
				{Sequence: 1, Function: "CreateProject"},
				{Sequence: 2, Function: "CreateComponent"},
				{Sequence: 3, Function: "GetEnvironments"},
				{Sequence: 4, Function: "WaitForBuild"},
				{Sequence: 5, Function: "DeployComponent"},
				{Sequence: 6, Function: "Promote"},
				{Sequence: 7, Function: "InvokeDevEndpoint"},
				{Sequence: 8, Function: "InvokeProdEndpoint"},
			},
			expectSuccess: true,
		},
		// Invalid order: CreateComponent before CreateProject
		{
			name: "Invalid: CreateComponent before CreateProject",
			steps: []Step{
				{Sequence: 1, Function: "CreateComponent"},
				{Sequence: 2, Function: "CreateProject"},
			},
			expectSuccess: false,
			expectError:   "ERROR: Cannot execute CreateComponent (Sequence 1) because dependency CreateProject is missing or comes later!",
		},

		// Invalid order: DeployComponent before WaitForBuild
		{
			name: "Invalid: DeployComponent before WaitForBuild",
			steps: []Step{
				{Sequence: 1, Function: "CreateProject"},
				{Sequence: 2, Function: "CreateComponent"},
				{Sequence: 3, Function: "DeployComponent"},
				{Sequence: 4, Function: "WaitForBuild"},
			},
			expectSuccess: false,
			expectError:   "ERROR: Cannot execute DeployComponent (Sequence 3) because dependency WaitForBuild is missing or comes later!",
		},
	}

	// Run each test case
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {

			stepsJSON, err := json.Marshal(tt.steps)
			if err != nil {
				t.Fatalf("Failed to marshal steps: %v", err)
			}

			err = ValidateSequenceOrder(string(stepsJSON))

			if tt.expectSuccess && err != nil {
				t.Errorf("Expected success but got error: %v", err)
			} else if !tt.expectSuccess && err == nil {
				t.Errorf("Expected error but got success")
			} else if !tt.expectSuccess && err != nil && err.Error() != tt.expectError {
				t.Errorf("Expected error: %s, but got: %s", tt.expectError, err.Error())
			}
		})
	}
}

var jsonData = `
[
	{
		"sequence": 1,
		"function": "CreateProject"
	},
	{
		"sequence": 2,
		"function": "CreateComponent"
	},
	{
		"sequence": 3,
		"function": "WaitForBuild"
	},
	{
		"sequence": 4,
		"function": "GetEnvironments"
	},
	{
		"sequence": 5,
		"function": "DeployComponent"
	},
	{
		"sequence": 6,
		"function": "Promote"
	},
	{
		"sequence": 7,
		"function": "InvokeDevEndpoint"
	},
	{
		"sequence": 8,
		"function": "InvokeProdEndpoint"
	}
]`

// Test case for validating JSON data
func TestValidateSequenceOrderWithJSON(t *testing.T) {
	// Validate sequence order with the JSON string
	err := ValidateSequenceOrder(jsonData)

	// Check if validation passed
	if err != nil {
		t.Errorf("Expected success but got error: %v", err)
	}
}
