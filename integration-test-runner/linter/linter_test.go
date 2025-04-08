package linter

import (
	"testing"
)

func TestValidateSequenceOrderWithCorrectPlaceholders(t *testing.T) {
	validJSON := `[
        {
            "function": "CreateProject",
            "params": {
                "region": "US",
                "placeholder": "project1"
            }
        },
        {
            "function": "CreateComponent",
            "params": {
                "project": "project1",
                "placeholder": "BallerinaServiceComponent"
            }
        },
        {
            "function": "WaitForBuild",
            "params": {
                "component": "BallerinaServiceComponent"
            }
        },
        {
            "function": "GetEnvironments",
            "params": {
                "project": "project1"
            }
        },
        {
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

func TestValidateSequenceOrderWithIncorrectOrder(t *testing.T) {
	invalidJSON := `[
        {
            "function": "CreateComponent",
            "params": {
                "project": "project1",
                "placeholder": "BallerinaServiceComponent"
            }
        },
        {
            "function": "CreateProject",
            "params": {
                "region": "US",
                "placeholder": "project1"
            }
        }
    ]`

	err := ValidateSequenceOrder(invalidJSON)
	if err == nil {
		t.Errorf("Expected error due to dependency being executed out of order")
	}
}

func TestValidateSequenceOrderWithPlaceholderUsedBeforeCreation(t *testing.T) {
	invalidJSON := `[
        {
            "function": "DeployComponent",
            "params": {
                "component": "BallerinaServiceComponent"
            }
        },
        {
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

func TestValidateSequenceOrderWithMultipleIndependentProjects(t *testing.T) {
	validJSON := `[
        {
            "function": "CreateProject",
            "params": {
                "region": "US",
                "placeholder": "ProjectA"
            }
        },
        {
            "function": "CreateComponent",
            "params": {
                "project": "ProjectA",
                "placeholder": "ComponentA"
            }
        },
        {
            "function": "CreateProject",
            "params": {
                "region": "EU",
                "placeholder": "ProjectB"
            }
        },
        {
            "function": "CreateComponent",
            "params": {
                "project": "ProjectB",
                "placeholder": "ComponentB"
            }
        }
    ]`

	err := ValidateSequenceOrder(validJSON)
	if err != nil {
		t.Errorf("Expected no error, but got: %v", err)
	}
}

func TestComponentFromProjectAUsedInProjectB(t *testing.T) {
	invalidJSON := `[
        {
            "function": "CreateProject",
            "params": {
                "placeholder": "ProjectA"
            }
        },
        {
            "function": "CreateComponent",
            "params": {
                "project": "ProjectA",
                "placeholder": "ComponentA"
            }
        },
        {
            "function": "CreateProject",
            "params": {
                "placeholder": "ProjectB"
            }
        },
        {
            "function": "DeployComponent",
            "params": {
                "component": "ComponentA"
            }
        }
    ]`

	err := ValidateSequenceOrder(invalidJSON)
	if err != nil {
		t.Errorf("Expected no error, but got: %v", err)
	}
}
