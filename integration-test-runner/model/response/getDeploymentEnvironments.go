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

type Environment struct {
	Name               string   `json:"name"`
	Id                 string   `json:"id"`
	ChoreoEnv          string   `json:"choreoEnv"`
	Vhost              string   `json:"vhost"`
	ApiEnvName         string   `json:"apiEnvName"`
	IsMigrating        bool     `json:"isMigrating"`
	ApimEnvId          string   `json:"apimEnvId"`
	Namespace          string   `json:"namespace"`
	SandboxVhost       string   `json:"sandboxVhost"`
	Critical           bool     `json:"critical"`
	IsPdp              bool     `json:"isPdp"`
	PromoteFrom        []string `json:"promoteFrom"`
	DpId               string   `json:"dpId"`
	TemplateId         string   `json:"templateId"`
	ScaleToZeroEnabled bool     `json:"scaleToZeroEnabled"`
}

type GetDeploymentEnvironments struct {
	Environments []Environment `json:"environments"`
}

func (e *Environment) GetKeyType() string {
	if e.Critical {
		return "Production"
	}

	return "Development"
}
