/*
 * Copyright © 2025 WSO2 LLC. (http://www.wso2.com).
 *
 * This software is the property of WSO2 LLC and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you've
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package response

type GetComponentDeployment struct {
	ComponentDeployment ComponentDeployment `json:"componentDeployment"`
}

type ComponentDeployment struct {
	EnvironmentID      string `json:"environmentId"`
	APIID              string `json:"apiId"`
	ReleaseID          string `json:"releaseId"`
	Build              Build  `json:"build"`
	InvokeURL          string `json:"invokeUrl"`
	VersionID          string `json:"versionId"`
	DeploymentStatus   string `json:"deploymentStatus"`
	DeploymentStatusV2 string `json:"deploymentStatusV2"`
	Version            string `json:"version"`
	Cron               string `json:"cron"`
	CronTimezone       string `json:"cronTimezone"`
}

type Build struct {
	BuildID                     string                      `json:"buildId"`
	DeployedAt                  string                      `json:"deployedAt"`
	Commit                      Commit                      `json:"commit"`
	SourceConfigMigrationStatus SourceConfigMigrationStatus `json:"sourceConfigMigrationStatus"`
	RunID                       string                      `json:"runId"`
}

type SourceConfigMigrationStatus struct {
	CanMigrate                bool        `json:"canMigrate"`
	ExistingFileName          interface{} `json:"existingFileName"`
	ExistingFileSchemaVersion interface{} `json:"existingFileSchemaVersion"`
}
