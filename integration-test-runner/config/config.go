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

package config

import (
	"embed"
	"errors"
	"os"

	yaml "gopkg.in/yaml.v3"
)

//go:embed dev-env-config.yaml
var devConfig embed.FS

//go:embed staging-env-config.yaml
var stageConfig embed.FS

//go:embed prod-env-config.yaml
var prodConfig embed.FS

var cfg map[string]map[string]string

func LoadConfigs(name string) error {
	content := selectConfig(name)

	yamlFile, err := content.ReadFile(name)

	if err != nil {
		return err
	}

	err = yaml.Unmarshal(yamlFile, &cfg)

	if err != nil {
		return err
	}

	return validateConfig()
}

func GetConfig(def Definition) (string, error) {
	if cfg == nil {
		return "", errors.New("config not loaded")
	}

	value, isExists := os.LookupEnv(def.String())

	if isExists {
		return value, nil
	}

	for _, v := range cfg {
		value, isExists = v[def.String()]

		if isExists {
			return value, nil
		}
	}

	return "", errors.New("config not found")
}

func validateConfig() error {
	defSet := make(map[string]interface{})
	for i := Definition(_begin_def_ + 1); i < Definition(_end_def_); i++ {
		defSet[i.String()] = nil
	}

	optSet := make(map[string]interface{})
	for i := OptionalDefinition(_begin_opt_ + 1); i < OptionalDefinition(_end_opt_); i++ {
		optSet[i.String()] = nil
	}

	for _, areas := range cfg {
		for k := range areas {
			if _, isExists := defSet[k]; !isExists {
				if _, isExists := optSet[k]; !isExists {
					return errors.New("yaml contains config '" + k +
						"' which is not defined in Config enum")
				}
			}
		}
	}

	return nil
}

func selectConfig(name string) embed.FS {
	switch name {
	case "dev-env-config.yaml":
		return devConfig
	case "staging-env-config.yaml":
		return stageConfig
	case "prod-env-config.yaml":
		return prodConfig
	default:
		return devConfig
	}
}
