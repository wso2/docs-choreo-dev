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
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"errors"
	"net/http"
	"strings"

	"github.com/go-resty/resty/v2"
)

func GetApiKey(client *resty.Client, request *request.GetApiKey) (*response.GetApiKey, error) {

	stsHost, err := config.GetConfig(config.STS_ENDPOINT)

	if err != nil {
		return nil, err
	}

	var url strings.Builder
	url.Grow(100)

	url.WriteString(stsHost)
	url.WriteString(apipublisher)
	url.WriteString("/")
	url.WriteString(request.ApiId)
	url.WriteString("/generate-key?organizationId=")
	url.WriteString(request.OrgUuid)
	url.WriteString("&keyType=")
	url.WriteString(request.KeyType)

	var response response.GetApiKey

	res, err := client.R().
		SetResult(&response).
		Post(url.String())

	if err != nil {
		return nil, err
	}

	if res.StatusCode() != http.StatusOK {
		return nil, errors.New("api key generation call failed, response code: " + res.Status())
	}

	return &response, nil
}
