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

type getBuildImages struct {
	params *GetBuildImagesParams
	state  *runner.SpecState
}

type GetBuildImagesParams struct {
	CompDetails *response.GetComponentDetails
}

func GetBuildImages(state *runner.SpecState, params *GetBuildImagesParams) *getBuildImages {
	return &getBuildImages{
		params: params,
		state:  state,
	}
}

func (g *getBuildImages) Execute(client *resty.Client) (unit.UnitComplete, error) {
	latestApiVersion, err := g.params.CompDetails.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	buildImageReq := request.GetBuildImages{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
	}

	buildImageRes, err := api.GetBuildImages(client, buildImageReq)

	if err != nil {
		return false, err
	}

	g.state.SetBuildImages(runner.BuildImagesKey{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
	}, *buildImageRes)

	return true, nil
}

func (g *getBuildImages) Name() string {
	return "getBuildImages"
}

func (g *getBuildImages) WaitTill() int64 {
	return 0
}
