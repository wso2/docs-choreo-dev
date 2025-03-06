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

type APIVersion struct {
	APIVersion        string   `json:"apiVersion"`
	ProxyName         string   `json:"proxyName"`
	ProxyURL          string   `json:"proxyUrl"`
	ProxyId           string   `json:"proxyId"`
	Id                string   `json:"id"`
	State             *string  `json:"state"`
	Latest            bool     `json:"latest"`
	Branch            *string  `json:"branch"`
	Accessibility     string   `json:"accessibility"`
	VersionId         *string  `json:"versionId"`
	AppEnvVersions    []string `json:"appEnvVersions"`
	AutoDeployEnabled bool     `json:"autoDeployEnabled"`
}

type DeploymentTrack struct {
	Id                string  `json:"id"`
	CreatedAt         string  `json:"createdAt"`
	UpdatedAt         string  `json:"updatedAt"`
	APIVersion        string  `json:"apiVersion"`
	Branch            *string `json:"branch"`
	Description       *string `json:"description"`
	ComponentId       string  `json:"componentId"`
	Latest            bool    `json:"latest"`
	VersionStrategy   string  `json:"versionStrategy"`
	AutoDeployEnabled bool    `json:"autoDeployEnabled"`
}

type Component struct {
	Id                      string            `json:"id"`
	Name                    string            `json:"name"`
	Handler                 string            `json:"handler"`
	Description             string            `json:"description"`
	DisplayType             string            `json:"displayType"`
	DisplayName             string            `json:"displayName"`
	OwnerName               *string           `json:"ownerName"`
	OrgId                   int               `json:"orgId"`
	OrgHandler              string            `json:"orgHandler"`
	Version                 string            `json:"version"`
	Labels                  []string          `json:"labels"`
	CreatedAt               string            `json:"createdAt"`
	ProjectId               string            `json:"projectId"`
	ApiId                   *string           `json:"apiId"`
	HttpBased               bool              `json:"httpBased"`
	IsMigrationCompleted    bool              `json:"isMigrationCompleted"`
	SkipDeploy              bool              `json:"skipDeploy"`
	EndpointShortURLEnabled bool              `json:"endpointShortUrlEnabled"`
	IsUnifiedConfigMapping  bool              `json:"isUnifiedConfigMapping"`
	ServiceAccessMode       *string           `json:"serviceAccessMode"`
	APIVersions             []APIVersion      `json:"apiVersions"`
	DeploymentTracks        []DeploymentTrack `json:"deploymentTracks"`
}

type GetComponentDetails struct {
	Component Component `json:"component"`
}
