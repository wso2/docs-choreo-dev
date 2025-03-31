package linter

import (
	"testing"
)

func TestValidateSequenceOrderWithCorrectPlaceholders(t *testing.T) {
	validJSON := `[
        {
            "sequence": 1,
            "function": "CreateProject",
            "params": {
                "region": "US",
                "placeholder": "project1"
            }
        },
        {
            "sequence": 2,
            "function": "CreateComponent",
            "params": {
                "project": "project1",
                "placeholder": "BallerinaServiceComponent"
            }
        },
        {
            "sequence": 3,
            "function": "WaitForBuild",
            "params": {
                "component": "BallerinaServiceComponent"
            }
        },
        {
            "sequence": 4,
            "function": "GetEnvironments",
            "params": {
                "project": "project1"
            }
        },
        {
            "sequence": 5,
            "function": "DeployComponent",
            "params": {
                "component": "BallerinaServiceComponent"
            }
        }
    ]`

	err := ValidateSequenceOrder(validJSON)
	if err != nil {
		t.Errorf("Expected no error, but got: %v", err)
	}
}

func TestValidateSequenceOrderWithMismatchedPlaceHolders(t *testing.T) {
	invalidJSON := `[
        {
            "sequence": 1,
            "function": "CreateProject",
            "params": {
                "region": "US",
                "placeholder": "Project1"
            }
        },
        {
            "sequence": 2,
            "function": "CreateComponent",
            "params": {
                "project": "Project2",
                "placeholder": "BallerinaServiceComponent"
            }
        }
    ]`

	err := ValidateSequenceOrder(invalidJSON)
	if err == nil {
		t.Errorf("Expected error due to mismatched placeholder reference")
	}
}

// Test case to check function executes before its Sequence dependency
func TestValidateSequenceOrderWithIncorrectOrder(t *testing.T) {
	invalidJSON := `[
        {
            "sequence": 2,
            "function": "CreateComponent",
            "params": {
                "project": "project1",
                "placeholder": "BallerinaServiceComponent"
            }
        },
        {
            "sequence": 1,
            "function": "CreateProject",
            "params": {
                "region": "US",
                "placeholder": "project1"
            }
        }
    ]`

	err := ValidateSequenceOrder(invalidJSON)
	if err == nil {
		t.Errorf("Expected error due to Sequence dependency being executed out of order")
	}
}

// Test case where a placeholder is used before being created
func TestValidateSequenceOrderWithPlaceholderUsedBeforeCreation(t *testing.T) {
	invalidJSON := `[
        {
            "sequence": 2,
            "function": "DeployComponent",
            "params": {
                "component": "BallerinaServiceComponent"
            }
        },
        {
            "sequence": 1,
            "function": "CreateComponent",
            "params": {
                "project": "project1",
                "placeholder": "BallerinaServiceComponent"
            }
        }
    ]`

	err := ValidateSequenceOrder(invalidJSON)
	if err == nil {
		t.Errorf("Expected error due to placeholder being used before creation")
	}
}

// Test case with multiple independent projects ensuring no cross-project dependencies
func TestValidateSequenceOrderWithMultipleIndependentProjects(t *testing.T) {
	validJSON := `[
        {
            "sequence": 1,
            "function": "CreateProject",
            "params": {
                "region": "US",
                "placeholder": "ProjectA"
            }
        },
        {
            "sequence": 2,
            "function": "CreateComponent",
            "params": {
                "project": "ProjectA",
                "placeholder": "ComponentA"
            }
        },
        {
            "sequence": 3,
            "function": "CreateProject",
            "params": {
                "region": "EU",
                "placeholder": "ProjectB"
            }
        },
        {
            "sequence": 4,
            "function": "CreateComponent",
            "params": {
                "project": "ProjectB",
                "placeholder": "ComponentB"
            }
        }
    ]`

	err := ValidateSequenceOrder(validJSON)
	if err != nil {
		t.Errorf("Expected error: %v", err)
	}
}

// Test case where a component from one component is used in another project
func TestComponentFromProjectAUsedInProjectB(t *testing.T) {
	content := `[
		{"sequence": 1, "function": "CreateProject", "params": {"placeholder": "ProjectA"}},
		{"sequence": 2, "function": "CreateComponent", "params": {"placeholder": "ComponentA"}},
		{"sequence": 3, "function": "CreateProject", "params": {"placeholder": "ProjectB"}},
		{"sequence": 4, "function": "DeployComponent", "params": {"placeholder": "ComponentA"}}
	]`

	err := ValidateSequenceOrder(content)
	if err != nil {
		t.Errorf("expected error: %v", err)
	}
}
