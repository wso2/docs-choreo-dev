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

package matcher

import (
	"testing"
)

func TestJsonMatchSimple(t *testing.T) {
	json1 := `{"name":"John","age":30,"city":"New York"}`
	json2 := `{"name":"John","age":30,"city":"New York"}`
	result, err := JsonMatch([]byte(json1), []byte(json2))

	if err != nil {
		t.Error("JsonMatch failed with error", err)
	}

	if result.Match == false {
		t.Error("JsonMatch failed", result.ErrorMsgs)
	}
}

func TestJsonMatchIgnore(t *testing.T) {
	json1 := `{"name":"@ignore@","age":30,"city":"New York"}`
	json2 := `{"name":"John","age":30,"city":"New York"}`
	result, err := JsonMatch([]byte(json1), []byte(json2))

	if err != nil {
		t.Error("JsonMatch failed with error", err)
	}

	if result.Match == false {
		t.Error("JsonMatch failed", result.ErrorMsgs)
	}
}

func TestJsonMatchFailed(t *testing.T) {
	json1 := `{"name":"Jack","age":25,"city":"New York"}`
	json2 := `{"name":"John","age":30,"city":"New York"}`
	result, err := JsonMatch([]byte(json1), []byte(json2))

	if err != nil {
		t.Error("JsonMatch failed with error", err)
	}

	if result.Match == true {
		t.Error("JsonMatch failed for mismatch")
	}

	if len(result.ErrorMsgs) == 0 {
		t.Error("JsonMatch no error messages returned")
	}

	erorMsg1 := "Expected values 'Jack' does not match actual value 'John'"
	erorMsg2 := "Expected values '25' does not match actual value '30'"

	var foundMsg1, foundMsg2 bool

	for _, msg := range result.ErrorMsgs {
		if msg == erorMsg1 {
			foundMsg1 = true
		}

		if msg == erorMsg2 {
			foundMsg2 = true
		}
	}

	if !foundMsg1 || !foundMsg2 {
		t.Error("JsonMatch error strings do not match")
	}

}

func TestJsonMatchAddionalValues(t *testing.T) {
	json1 := `{"name":"John","age":30,"city":"New York"}`
	json2 := `{"name":"John","age":30,"city":"New York","country":"USA"}`
	result, err := JsonMatch([]byte(json1), []byte(json2))

	if err != nil {
		t.Error("JsonMatch failed with error", err)
	}

	if result.Match == false {
		t.Error("JsonMatch failed for additional values", result.ErrorMsgs)
	}

}

func TestJsonMatchComplex(t *testing.T) {
	json1 := `{"name":"John","age":30,"city":"New York","hobbies":["music", "reading"],"address":{"street":"123 Main St","zip":12345}}`
	json2 := `{"name":"John","age":30,"city":"New York","hobbies":["reading","music"],"address":{"street":"123 Main St","zip":12345}}`
	result, err := JsonMatch([]byte(json1), []byte(json2))

	if err != nil {
		t.Error("JsonMatch failed with error", err)
	}

	if result.Match == false {
		t.Error("JsonMatch failed for complex json", result.ErrorMsgs)
	}

}

func TestJsonMatchComplexFail(t *testing.T) {
	json1 := `{"name":"John","age":30,"city":"New York","hobbies":["reading","music","hiking"],"address":{"street":"123 Main St","zip":12345}}`
	json2 := `{"name":"John","age":30,"city":"New York","hobbies":["reading","music"],"address":{"street":"123 Main St","zip":56789}}`
	result, err := JsonMatch([]byte(json1), []byte(json2))

	if err != nil {
		t.Error("JsonMatch failed with error", err)
	}

	if result.Match == true {
		t.Error("JsonMatch failed for complex json mismatch")
	}

	if len(result.ErrorMsgs) == 0 {
		t.Error("JsonMatch no error messages returned")
	}
}

func TestJsonMatchChoreoComplex(t *testing.T) {
	expected := `{
					"component": {
						"id": "@ignore@",
						"name": "autotest1734588022637oas",
						"handler": "autotest1734588022637oas",
						"description": " ",
						"displayType": "proxy",
						"displayName": "autotest1734588022637oas",
						"ownerName": null,
						"orgId": 421,
						"orgHandler": "uvindradiasjayasinha",
						"version": "v1.0",
						"labels": [],
						"createdAt": "2024-12-19T06:00:30.443Z",
						"projectId": "f98a5c31-1b87-4404-9a3b-6e3d79c04009",
						"apiId": null,
						"httpBased": true,
						"isMigrationCompleted": true,
						"skipDeploy": false,
						"endpointShortUrlEnabled": false,
						"isUnifiedConfigMapping": false,
						"serviceAccessMode": null,
						"apiVersions": [
							{
								"apiVersion": "v1.0",
								"proxyName": "autotest1734588022637oas",
								"proxyUrl": "/7ffcb4dd-ed30-43c7-8a9c-d8e41f9f0c45/autotest1734588015451/1734588022637",
								"proxyId": "6763b67daddb9639e2e43fab",
								"id": "6763b67daddb9639e2e43fab",
								"state": null,
								"latest": true,
								"branch": null,
								"accessibility": "external",
								"versionId": null,
								"appEnvVersions": [],
								"autoDeployEnabled": false
							}
						],
						"deploymentTracks": [
							{
								"id": "6763b67daddb9639e2e43fab",
								"createdAt": "1734588029960",
								"updatedAt": "2024-12-19 06:13:33.297",
								"apiVersion": "v1.0",
								"branch": null,
								"description": null,
								"componentId": "72e99377-a2ae-45e5-9278-8fb4e3950d4e",
								"latest": true,
								"versionStrategy": "",
								"autoDeployEnabled": false
							}
						]
					}
				}`

	actual := `{
					"component": {
						"id": "72e99377-a2ae-45e5-9278-8fb4e3950d4e",
						"name": "autotest1734588022637oas",
						"handler": "autotest1734588022637oas",
						"description": " ",
						"displayType": "proxy",
						"displayName": "autotest1734588022637oas",
						"ownerName": null,
						"orgId": 421,
						"orgHandler": "uvindradiasjayasinha",
						"version": "v1.0",
						"labels": [],
						"createdAt": "2024-12-19T06:00:30.443Z",
						"projectId": "f98a5c31-1b87-4404-9a3b-6e3d79c04009",
						"apiId": null,
						"httpBased": true,
						"isMigrationCompleted": true,
						"skipDeploy": false,
						"endpointShortUrlEnabled": false,
						"isUnifiedConfigMapping": false,
						"serviceAccessMode": null,
						"apiVersions": [
							{
								"apiVersion": "v1.0",
								"proxyName": "autotest1734588022637oas",
								"proxyUrl": "/7ffcb4dd-ed30-43c7-8a9c-d8e41f9f0c45/autotest1734588015451/1734588022637",
								"proxyId": "6763b67daddb9639e2e43fab",
								"id": "6763b67daddb9639e2e43fab",
								"state": null,
								"latest": true,
								"branch": null,
								"accessibility": "external",
								"versionId": null,
								"appEnvVersions": [],
								"autoDeployEnabled": false
							}
						],
						"deploymentTracks": [
							{
								"id": "6763b67daddb9639e2e43fab",
								"createdAt": "1734588029960",
								"updatedAt": "2024-12-19 06:13:33.297",
								"apiVersion": "v1.0",
								"branch": null,
								"description": null,
								"componentId": "72e99377-a2ae-45e5-9278-8fb4e3950d4e",
								"latest": true,
								"versionStrategy": "",
								"autoDeployEnabled": false
							}
						]
					}
				}`

	result, err := JsonMatch([]byte(expected), []byte(actual))

	if err != nil {
		t.Error("JsonMatch failed with error", err)
	}

	if result.Match == false {
		for _, msg := range result.ErrorMsgs {
			t.Error("JsonMatch failed for complex json: ", msg)
		}
	}
}

func TestJsonMatchChoreoComplexFail(t *testing.T) {
	expected := `{
					"component": {
						"id": "@ignore@",
						"name": "autotest1734588022637oas",
						"handler": "autotest1734588022637oas",
						"description": " ",
						"displayType": "proxy",
						"displayName": "autotest1734588022637oas",
						"ownerName": null,
						"orgId": 421,
						"orgHandler": "uvindradiasjayasinha",
						"version": "v1.0",
						"labels": [],
						"createdAt": "2024-12-19T06:00:30.443Z",
						"projectId": "f98a5c31-1b87-4404-9a3b-6e3d79c04009",
						"apiId": null,
						"httpBased": true,
						"isMigrationCompleted": true,
						"skipDeploy": false,
						"endpointShortUrlEnabled": false,
						"isUnifiedConfigMapping": false,
						"serviceAccessMode": null,
						"apiVersions": [
							{
								"apiVersion": "v1.0",
								"proxyName": "autotest1734588022637oas",
								"proxyUrl": "/7ffcb4dd-ed30-43c7-8a9c-d8e41f9f0c45/autotest1734588015451/1734588022637",
								"proxyId": "6763b67daddb9639e2e43fab",
								"id": "@ignore@",
								"state": null,
								"latest": true,
								"branch": null,
								"accessibility": "external",
								"versionId": null,
								"appEnvVersions": [],
								"autoDeployEnabled": false
							}
						],
						"deploymentTracks": [
							{
								"id": "@ignore@",
								"createdAt": "1734588029960",
								"updatedAt": "2024-12-19 06:13:33.297",
								"apiVersion": "v1.0",
								"branch": null,
								"description": null,
								"componentId": "72e99377-a2ae-45e5-9278-8fb4e3950d4e",
								"latest": true,
								"versionStrategy": "",
								"autoDeployEnabled": false
							}
						]
					}
				}`

	actual := `{
					"component": {
						"id": "72e99377-a2ae-45e5-9278-8fb4e3950d4e",
						"name": "autotest1734588022637oas",
						"handler": "autotest1734588022637oas",
						"description": " ",
						"displayType": "proxy",
						"displayName": "autotest1734588022637oas",
						"ownerName": null,
						"orgId": 421,
						"orgHandler": "uvindradiasjayasinha",
						"version": "v1.0",
						"labels": [],
						"createdAt": "2024-12-19T06:00:30.443Z",
						"projectId": "f98a5c31-1b87-4404-9a3b-6e3d79c04009",
						"apiId": null,
						"httpBased": true,
						"isMigrationCompleted": true,
						"skipDeploy": false,
						"endpointShortUrlEnabled": false,
						"isUnifiedConfigMapping": false,
						"serviceAccessMode": null,
						"apiVersions": [
							{
								"apiVersion": "v1.0",
								"proxyName": "someotherproxyname",
								"proxyUrl": "/7ffcb4dd-ed30-43c7-8a9c-d8e41f9f0c45/autotest1734588015451/1734588022637",
								"proxyId": "6763b67daddb9639e2e43fab",
								"id": "6763b67daddb9639e2e43fab",
								"state": null,
								"latest": true,
								"branch": null,
								"accessibility": "external",
								"versionId": null,
								"appEnvVersions": [],
								"autoDeployEnabled": false
							}
						],
						"deploymentTracks": [
							{
								"id": "6763b67daddb9639e2e43fab",
								"createdAt": "1734588029960",
								"updatedAt": "2024-12-19 06:13:33.297",
								"apiVersion": "v1.0",
								"branch": null,
								"description": null,
								"componentId": "72e99377-a2ae-45e5-9278-8fb4e3950d4e",
								"latest": true,
								"versionStrategy": "",
								"autoDeployEnabled": false
							}
						]
					}
				}`

	result, err := JsonMatch([]byte(expected), []byte(actual))

	if err != nil {
		t.Error("JsonMatch failed with error", err)
	}

	if result.Match == true {
		t.Error("JsonMatch passed when it should have failed")
	}
}

func TestJsonMatchChoreoSubset(t *testing.T) {
	expected := `{
					"component": {
						"createdAt": "@ignore@",
						"displayName": "autotest8d68-1742109031",
						"displayType": "ballerinaService",
						"handler": "autotest8d68-1742109031",
						"id": "@ignore@",
						"labels": [],
						"name": "autotest8d68-1742109031",
						"orgHandler": "choreointegrationtestsuserdevdlwqo",
						"orgId": 3752,
						"projectId": "63d2a016-225f-4c96-9583-6a3220c998cb",
						"version": "v1.0"
					}
				}`

	actual := `{
					"component": {
						"apiId": null,
						"apiVersions": [
									{
										"environmentId": "8375d194-8e6a-4440-a7eb-f6b116ccb8ca",
										"releaseId": "089be22c-c117-4cc4-9661-0071bfc14e27",
										"release": {
											"id": "089be22c-c117-4cc4-9661-0071bfc14e27",
											"metadata": {
											"choreoEnv": "prod"
											},
											"environmentId": "8375d194-8e6a-4440-a7eb-f6b116ccb8ca",
											"environment": null,
											"gitHash": null,
											"gitOpsHash": null
										}
									},
									{
										"environmentId": "45ea2d80-c80d-45bb-880b-d3a7d24889c1",
										"releaseId": "6a2e5e29-da3e-4f5b-b5f3-837103693bab",
										"release": {
											"id": "6a2e5e29-da3e-4f5b-b5f3-837103693bab",
											"metadata": {
											"choreoEnv": "dev"
											},
											"environmentId": "45ea2d80-c80d-45bb-880b-d3a7d24889c1",
											"environment": null,
											"gitHash": null,
											"gitOpsHash": null
										}
									}
								],
						"createdAt": "2025-03-16T07:10:33.1885945Z",
						"description": " ",
						"displayName": "autotest8d68-1742109031",
						"displayType": "ballerinaService",
						"handler": "autotest8d68-1742109031",
						"httpBased": false,
						"id": "7e50a062-ec93-45b5-b9ed-c60bce63980a",
						"isMigrationCompleted": false,
						"labels": [],
						"name": "autotest8d68-1742109031",
						"orgHandler": "choreointegrationtestsuserdevdlwqo",
						"orgId": 3752,
						"ownerName": "null",
						"projectId": "63d2a016-225f-4c96-9583-6a3220c998cb",
						"repository":  {
								"nameApp": "byor-service-app1",
								"nameConfig": "8251aa127efa-autotest0795-1742029503-configs",
								"branch": "main",
								"branchApp": "main",
								"organizationApp": "choreo-test-apps",
								"organizationConfig": "choreo-userapps-gitops-dev",
								"isUserManage": true,
								"appSubPath": "",
								"byocBuildConfig": null
							},
						"version": "v1.0"
					}
				}`

	result, err := JsonMatch([]byte(expected), []byte(actual))

	if err != nil {
		t.Error("JsonMatch failed with error", err)
	}

	if result.Match == false {
		for _, msg := range result.ErrorMsgs {
			t.Error("JsonMatch failed for complex json: ", msg)
		}
	}
}
