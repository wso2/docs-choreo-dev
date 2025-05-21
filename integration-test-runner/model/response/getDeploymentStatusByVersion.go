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

package response

type Metadata struct {
	Governance       interface{} `json:"governance"`
	SourceConfigType interface{} `json:"sourceConfigType"`
	SchemaVersion    interface{} `json:"schemaVersion"`
}

type DeploymentStatus struct {
	ID                    int64       `json:"id"`
	Sha                   string      `json:"sha"`
	CompletedAt           string      `json:"completed_at"`
	StartedAt             string      `json:"started_at"`
	Name                  string      `json:"name"`
	Status                string      `json:"status"`
	Conclusion            string      `json:"conclusion"`
	ConclusionV2          string      `json:"conclusionV2"`
	IsAutoDeploy          bool        `json:"isAutoDeploy"`
	FailureReason         int         `json:"failureReason"`
	SourceCommitId        string      `json:"sourceCommitId"`
	IsTriggeredAtCreation bool        `json:"isTriggeredAtCreation"`
	BuildRef              string      `json:"buildRef"`
	ClusterId             interface{} `json:"clusterId"`
	GitRefType            string      `json:"gitRefType"`
	CommitTag             interface{} `json:"commitTag"`
	Metadata              Metadata    `json:"metadata"`
}

type GetDeploymentStatusByVersion struct {
	DeploymentStatusByVersion []DeploymentStatus `json:"deploymentStatusByVersion"`
}
