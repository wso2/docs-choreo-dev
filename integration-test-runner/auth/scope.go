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

package auth

import (
	"embed"

	"gopkg.in/yaml.v3"
)

//go:embed dev-scopes.yaml
var devScopes embed.FS

//go:embed stage-scopes.yaml
var stageScopes embed.FS

//go:embed prod-scopes.yaml
var prodScopes embed.FS

type scopes struct {
	Scopes []string `yaml:"scopes"`
}

func GetScopes(name string) ([]string, error) {
	content := selectScopeFile(name)

	yamlFile, err := content.ReadFile(name)

	if err != nil {
		return nil, err
	}

	var scp scopes

	err = yaml.Unmarshal(yamlFile, &scp)

	if err != nil {
		return nil, err
	}

	return scp.Scopes, nil
}

func selectScopeFile(name string) embed.FS {
	switch name {
	case "dev-env-config.yaml":
		return devScopes
	case "staging-env-config.yaml":
		return stageScopes
	case "prod-env-config.yaml":
		return prodScopes
	default:
		return devScopes
	}
}
