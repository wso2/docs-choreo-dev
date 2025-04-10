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

package component

import (
	"choreo-integration-test-runner/choreo/internal/api"
	"choreo-integration-test-runner/choreo/internal/unit"
	"choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"

	"github.com/go-resty/resty/v2"
)

type getApiKey struct {
	params *GetApiKeyParam
	state  *runner.SpecState
}

type GetApiKeyParam struct {
	CompDetails *response.GetComponentDetails
	Environment *response.Environment
}

func GetApiKey(params *GetApiKeyParam, state *runner.SpecState) *getApiKey {
	return &getApiKey{
		params: params,
		state:  state,
	}
}

func (g *getApiKey) Execute(client *resty.Client) (unit.UnitComplete, error) {
	latestApiVersion, err := g.params.CompDetails.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	releaseId, err := getEnvironmentReleaseId(latestApiVersion, g.params.Environment)

	if err != nil {
		return false, err
	}

	endpoints, err := g.state.Endpoints(runner.EndpointKey{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
		ReleaseId:   releaseId,
	})

	if err != nil {
		return false, err
	}

	key := runner.ApiKeyKey{
		ApiId:   endpoints.Endpoints[0].ApimId,
		OrgUuid: g.state.GetOrgUuid(),
		KeyType: g.params.Environment.GetKeyType(),
	}

	request := request.GetApiKey(key)

	response, err := api.GetApiKey(client, &request)

	if err != nil {
		return false, err
	}

	g.state.SetApiKey(key, *response)

	return true, nil
}

func (g *getApiKey) Name() string {
	return "getApiKey"
}

func (g *getApiKey) WaitTill() int64 {
	return 0
}
