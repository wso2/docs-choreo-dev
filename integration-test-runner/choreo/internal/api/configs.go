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
	"choreo-integration-test-runner/template"
	"errors"
	"net/http"
	"strings"

	"github.com/go-resty/resty/v2"
)

func AddConfiguration(client *resty.Client, request request.AddConfiguration) error {
	buf, err := template.PopulateRequestTemplate("addConfiguration", request)

	if err != nil {
		return err
	}

	newAppServiceHost, err := config.GetConfig(config.CHOREO_NEW_APP_SERVICE_ENDPOINT)

	if err != nil {
		return err
	}

	var url strings.Builder
	url.Grow(100)

	url.WriteString(newAppServiceHost)
	url.WriteString(configs)
	url.WriteString("/orgs/")
	url.WriteString(request.OrgHandle)
	url.WriteString("/projects/")
	url.WriteString(request.ProjectId)
	url.WriteString("/components/")
	url.WriteString(request.ComponentId)
	url.WriteString("/envs/")
	url.WriteString(request.EnvId)
	url.WriteString("/")
	url.WriteString(request.LatestVersionId)
	url.WriteString("/configurations")

	res, err := client.R().
		SetBody(buf.String()).
		Post(url.String())

	if err != nil {
		return err
	}

	if res.StatusCode() != http.StatusOK {
		return errors.New("configs call failed, response code: " + res.Status())
	}

	return nil
}
