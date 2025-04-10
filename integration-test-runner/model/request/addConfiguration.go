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

package request

type BalConfig struct {
	ConfigKeyName string
	IsRequired    bool
	ValueOrSource string
	ValueType     string
}

type AddConfiguration struct {
	ComponentId     string
	EnvId           string
	LatestVersionId string
	OrgHandle       string
	ProjectId       string
	ModuleName      string
	CommitHash      string
	ApplyNow        bool
	Operation       int
	SourceUuid      string
	Configs         []BalConfig
}
