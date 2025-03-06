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

type Project struct {
	Id                   string `json:"id"`
	OrgId                int    `json:"orgId"`
	Name                 string `json:"name"`
	CreatedDate          string `json:"createdDate"`
	Handler              string `json:"handler"`
	Region               string `json:"region"`
	Description          string `json:"description"`
	DeploymentPipelineId string `json:"deploymentPipelineId"`
	Type                 string `json:"type"`
	UpdatedAt            string `json:"updatedAt"`
}

type CreateProject struct {
	Project Project `json:"createProject"`
}
