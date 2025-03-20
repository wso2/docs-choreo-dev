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

package template

import (
	"choreo-integration-test-runner/model/request"
	"strings"
	"testing"
)

func TestReadTemplate(t *testing.T) {

	model := request.CreateProject{
		Name:           "Test Project",
		Description:    "Test Description",
		ProjectHandler: "testproject",
		OrgId:          1,
		OrgHandler:     "testorg",
		Region:         "US",
	}

	err := LoadTemplates()

	if err != nil {
		t.Fatalf("LoadTemplates returned an error: %v", err)
	}

	buf, err := PopulateRequestTemplate("createProject", model)

	if err != nil {
		t.Fatalf("readTemplate returned an error: %v", err)
	}

	// Validate the output
	expectedOutput := "name: \"Test Project\","

	if !strings.Contains(buf.String(), expectedOutput) {
		t.Errorf("Expected %q is not in %q", expectedOutput, buf.String())
	}
}

func TestReadTemplateFileNotFound(t *testing.T) {
	// Define the model
	model := struct {
		Name string
	}{
		Name: "World",
	}

	err := LoadTemplates()

	if err != nil {
		t.Fatalf("LoadTemplates returned an error: %v", err)
	}

	_, err = PopulateRequestTemplate("testTemplate", model)

	if err == nil {
		t.Fatalf("Expected an error but got nil")
	}
}
