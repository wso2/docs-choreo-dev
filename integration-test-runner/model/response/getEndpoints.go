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

type Endpoint struct {
	Id                string      `json:"id"`
	CreatedAt         string      `json:"createdAt"`
	UpdatedAt         string      `json:"updatedAt"`
	ReleaseId         string      `json:"releaseId"`
	EnvironmentId     string      `json:"environmentId"`
	DisplayName       string      `json:"displayName"`
	Port              int         `json:"port"`
	Type              string      `json:"type"`
	ApiContext        string      `json:"apiContext"`
	ApiDefinitionPath string      `json:"apiDefinitionPath"`
	InvokeUrl         string      `json:"invokeUrl"`
	Visibility        string      `json:"visibility"`
	HostName          string      `json:"hostName"`
	ApimId            string      `json:"apimId"`
	ApimRevisionId    string      `json:"apimRevisionId"`
	ApimName          string      `json:"apimName"`
	ProjectUrl        string      `json:"projectUrl"`
	OrganizationUrl   string      `json:"organizationUrl"`
	PublicUrl         string      `json:"publicUrl"`
	State             string      `json:"state"`
	StateReason       StateReason `json:"stateReason"`
	IsDeleted         bool        `json:"isDeleted"`
	DeletedAt         string      `json:"deletedAt"`
}

type StateReason struct {
	Code     string `json:"code"`
	Message  string `json:"message"`
	Details  string `json:"details"`
	WorkerId string `json:"workerId"`
}

type GetEndpoints struct {
	Endpoints []Endpoint `json:"componentEndpoints"`
}
