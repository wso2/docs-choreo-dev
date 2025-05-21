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
	"bytes"
	"choreo-integration-test-runner/choreo/internal/unit"
	"choreo-integration-test-runner/helper/matcher"
	"choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/runner"
	"fmt"
	"strings"

	"github.com/go-resty/resty/v2"
)

type invokeUrl struct {
	params *InvokeUrlParams
	state  *runner.SpecState
}

type InvokeUrlParams struct {
	CompDetails        *response.GetComponentDetails
	Environment        *response.Environment
	HttpMethod         string
	ContentType        string
	ResourcePath       string
	QueryParams        string
	Request            string
	ExpectedStatusCode int
	ExpectedResponse   string
}

func InvokeUrl(state *runner.SpecState, params *InvokeUrlParams) *invokeUrl {
	return &invokeUrl{
		params: params,
		state:  state,
	}
}

func (g *invokeUrl) Execute(client *resty.Client) (unit.UnitComplete, error) {
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

	endpoint := endpoints.Endpoints[0]
	url := endpoint.PublicUrl + g.params.ResourcePath

	if g.params.QueryParams != "" {
		url += "?" + g.params.QueryParams
	}

	apiKey, err := g.state.ApiKey(runner.ApiKeyKey{
		ApiId:   endpoint.ApimId,
		OrgUuid: g.state.GetOrgUuid(),
		KeyType: g.params.Environment.GetKeyType(),
	})

	if err != nil {
		return false, err
	}

	request := client.R()
	request.SetHeader("Test-Key", apiKey.Apikey)

	if g.params.ContentType != "" {
		request.SetHeader("Content-Type", g.params.ContentType)
	}

	var response *resty.Response

	switch g.params.HttpMethod {
	case "GET":
		response, err = request.Get(url)
	case "POST":
		if g.params.Request != "" {
			request.SetBody(g.params.Request)
		}
		response, err = request.Post(url)
	case "PUT":
		if g.params.Request != "" {
			request.SetBody(g.params.Request)
		}
		response, err = request.Put(url)
	case "DELETE":
		response, err = request.Delete(url)
	case "PATCH":
		if g.params.Request != "" {
			request.SetBody(g.params.Request)
		}
		response, err = request.Patch(url)
	case "HEAD":
		response, err = request.Head(url)
	default:
		return false, fmt.Errorf("unsupported HTTP method: %s", g.params.HttpMethod)
	}

	if err != nil {
		return false, err
	}

	if response.StatusCode() != g.params.ExpectedStatusCode {
		return false, fmt.Errorf("expected status code %d, got %d", g.params.ExpectedStatusCode, response.StatusCode())
	}

	if g.params.ExpectedResponse != "" {
		if response.Header().Get("Content-Type") == "application/json" {
			result, err := matcher.JsonEqual([]byte(g.params.ExpectedResponse), response.Body())

			if err != nil {
				return false, err
			}
			if !result.Match {
				return false, fmt.Errorf("%s", strings.Join(result.ErrorMsgs, ","))
			}
		} else {
			actual := response.Body()

			actual = bytes.TrimSpace(actual)
			actual = bytes.TrimLeft(actual, "\r\n")
			actual = bytes.TrimRight(actual, "\r\n")

			if !bytes.Equal([]byte(g.params.ExpectedResponse), actual) {
				return false, fmt.Errorf("expected response %s(%d), got %s(%d)", g.params.ExpectedResponse, len(g.params.ExpectedResponse), string(response.Body()), len(string(response.Body())))
			}
		}
	}

	return true, nil
}

func (g *invokeUrl) Name() string {
	return "invokeUrl"
}

func (g *invokeUrl) WaitTill() int64 {
	return 0
}
