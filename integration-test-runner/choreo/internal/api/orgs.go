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

package api

import (
	"choreo-integration-test-runner/choreo/status"
	res "choreo-integration-test-runner/model/response"
	"net/http"

	"github.com/go-resty/resty/v2"
)

func GetOrgs(client *resty.Client) (*res.GetOrgs, status.Status) {
	response := res.GetOrgs{}

	res, err := client.R().
		SetResult(&response).
		Get(orgs)

	if err != nil {
		return nil, status.NewPermFailedStatus(err.Error())
	}

	if res.StatusCode() != http.StatusOK {
		return nil, status.NewTempFailedStatus(res.Status())
	}

	return &response, status.NewSuccessStatus()
}
