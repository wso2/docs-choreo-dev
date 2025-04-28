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
	"testing"
)

func TestValidateSequenceOrderWithCorrectPlaceholders(t *testing.T) {
	validJSON := `---
- action: CreateProject
  params:
    region: US
    description: Ballerina service project
    placeholder: testproject
- action: CreateComponent
  params:
    srcGitRepoURL: https://github.com/wso2/choreo-samples
    repositoryBranch: main
    repositorySubPath: greeting-service
    displayType: ballerinaService
    buildPack: Ballerina
    accessibility: external
    isPublicRepo: 'true'
    project: testproject
    placeholder: servicecomp
- action: WaitForBuild
  params:
    component: servicecomp
- action: GetEnvironments
  params:
    project: testproject
- action: DeployComponent
  params:
    component: servicecomp
- action: PromoteComponent
  params:
    component: servicecomp
- action: InvokeDeployment
  params:
    component: servicecomp
    method: GET
    resource: "/"
    queryParams: name=Hello
    statusCode: '200'
    response: |-
      {
        "from": "Choreo",
        "to": "Hello",
        "message": "Welcome to Choreo!"
      }
- action: InvokePromotion
  params:
    component: servicecomp
    method: GET
    resource: "/"
    queryParams: name=Hello
    statusCode: '200'
    response: |-
      {
        "from": "Choreo",
        "to": "Hello",
        "message": "Welcome to Choreo!"
      }
`
	l := NewLinter()

	err := l.ValidateSequenceOrder(validJSON)
	if err != nil {
		t.Errorf("Expected no error, but got: %v", err)
	}
}

func TestValidateSequenceOrderWithIncorrectOrder(t *testing.T) {
	invalidJSON := `---
- action: CreateComponent
  params:
    project: project1
    placeholder: BallerinaServiceComponent
- action: CreateProject
  params:
    region: US
    placeholder: project1`

	l := NewLinter()

	err := l.ValidateSequenceOrder(invalidJSON)
	if err == nil {
		t.Errorf("Expected error due to dependency being executed out of order")
	}
}

func TestValidateSequenceOrderWithPlaceholderUsedBeforeCreation(t *testing.T) {
	invalidJSON := `---
- action: DeployComponent
  params:
    component: BallerinaServiceComponent
- action: CreateComponent
  params:
    project: project1
    placeholder: BallerinaServiceComponent
`
	l := NewLinter()

	err := l.ValidateSequenceOrder(invalidJSON)
	if err == nil {
		t.Errorf("Expected error due to placeholder being used before creation")
	}
}

func TestValidateSequenceOrderWithMultipleIndependentProjects(t *testing.T) {
	validJSON := `---
- action: CreateProject
  params:
    region: US
    placeholder: ProjectA
- action: CreateComponent
  params:
    project: ProjectA
    placeholder: ComponentA
- action: CreateProject
  params:
    region: EU
    placeholder: ProjectB
- action: CreateComponent
  params:
    project: ProjectB
    placeholder: ComponentB
`
	l := NewLinter()

	err := l.ValidateSequenceOrder(validJSON)
	if err != nil {
		t.Errorf("Expected no error, but got: %v", err)
	}
}

func TestComponentFromProjectAUsedInProjectB(t *testing.T) {
	invalidJSON := `---
- action: CreateProject
  params:
    placeholder: ProjectA
- action: CreateComponent
  params:
    project: ProjectA
    placeholder: ComponentA
- action: CreateProject
  params:
    placeholder: ProjectB
- action: CreateComponent
  params:
    project: ProjectB
    placeholder: ComponentA
`

	l := NewLinter()

	err := l.ValidateSequenceOrder(invalidJSON)
	if err == nil {
		t.Errorf("Expected error due to same component placeholder being used in multiple projects")
	}
}
