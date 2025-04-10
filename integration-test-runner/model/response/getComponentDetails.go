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

import "errors"

type AppEnvVersion struct {
	EnvironmentId string  `json:"environmentId"`
	ReleaseId     string  `json:"releaseId"`
	Release       Release `json:"release"`
}

type Release struct {
	Id            string          `json:"id"`
	Metadata      ReleaseMetadata `json:"metadata"`
	EnvironmentId string          `json:"environmentId"`
	Environment   interface{}     `json:"environment"`
	GitHash       interface{}     `json:"gitHash"`
	GitOpsHash    interface{}     `json:"gitOpsHash"`
}

type ReleaseMetadata struct {
	ChoreoEnv string `json:"choreoEnv"`
}

type Repository struct {
	NameApp            string      `json:"nameApp"`
	NameConfig         string      `json:"nameConfig"`
	Branch             string      `json:"branch"`
	BranchApp          string      `json:"branchApp"`
	OrganizationApp    string      `json:"organizationApp"`
	OrganizationConfig string      `json:"organizationConfig"`
	IsUserManage       bool        `json:"isUserManage"`
	AppSubPath         string      `json:"appSubPath"`
	ByocBuildConfig    interface{} `json:"byocBuildConfig"`
}

type ApiVersion struct {
	ApiVersion     string          `json:"apiVersion"`
	ProxyName      string          `json:"proxyName"`
	ProxyUrl       string          `json:"proxyUrl"`
	ProxyId        interface{}     `json:"proxyId"`
	Id             string          `json:"id"`
	State          interface{}     `json:"state"`
	Latest         bool            `json:"latest"`
	Branch         string          `json:"branch"`
	Accessibility  string          `json:"accessibility"`
	AppEnvVersions []AppEnvVersion `json:"appEnvVersions"`
}

type Component struct {
	Id                   string       `json:"id"`
	Name                 string       `json:"name"`
	Handler              string       `json:"handler"`
	Description          string       `json:"description"`
	DisplayType          string       `json:"displayType"`
	DisplayName          string       `json:"displayName"`
	OwnerName            string       `json:"ownerName"`
	OrgId                int          `json:"orgId"`
	OrgHandler           string       `json:"orgHandler"`
	Version              string       `json:"version"`
	Labels               []string     `json:"labels"`
	CreatedAt            string       `json:"createdAt"`
	ProjectId            string       `json:"projectId"`
	ApiId                interface{}  `json:"apiId"`
	HttpBased            bool         `json:"httpBased"`
	IsMigrationCompleted bool         `json:"isMigrationCompleted"`
	Repository           Repository   `json:"repository"`
	ApiVersions          []ApiVersion `json:"apiVersions"`
}

type GetComponentDetails struct {
	Component Component `json:"component"`
}

func (c *GetComponentDetails) GetLatestApiVersion() (*ApiVersion, error) {
	for _, apiVersion := range c.Component.ApiVersions {
		if apiVersion.Latest {
			return &apiVersion, nil
		}
	}

	return nil, errors.New("latest version not found")
}
