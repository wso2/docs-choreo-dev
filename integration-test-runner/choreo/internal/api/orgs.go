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
	"choreo-integration-test-runner/config"
	res "choreo-integration-test-runner/model/response"
	"errors"
	"net/http"

	"github.com/go-resty/resty/v2"
)

func GetOrgs(client *resty.Client) (*res.GetOrgs, error) {
	response := res.GetOrgs{}

	newAppServiceHost, err := config.GetConfig(config.CHOREO_NEW_APP_SERVICE_ENDPOINT)

	if err != nil {
		return nil, err
	}

	res, err := client.R().
		SetResult(&response).
		Get(newAppServiceHost + orgs)

	if err != nil {
		return nil, err
	}

	if res.StatusCode() != http.StatusOK {
		return nil, errors.New("orgs call failed, response code: " + res.Status())
	}

	return &response, nil
}
