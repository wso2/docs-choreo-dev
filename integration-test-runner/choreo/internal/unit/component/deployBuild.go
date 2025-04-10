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
	"errors"

	"github.com/go-resty/resty/v2"
)

type deployBuild struct {
	params *DeployBuildParams
	state  *runner.SpecState
}

type DeployBuildParams struct {
	CompDetails   *response.GetComponentDetails
	Environment   *response.Environment
	CommitHistory *response.GetCommitHistory
}

func DeployBuild(state *runner.SpecState, params *DeployBuildParams) *deployBuild {
	return &deployBuild{
		params: params,
		state:  state,
	}
}

func (g *deployBuild) Execute(client *resty.Client) (unit.UnitComplete, error) {
	latestApiVersion, err := g.params.CompDetails.GetLatestApiVersion()

	if err != nil {
		return false, err
	}

	buildImagesRes, err := g.state.BuildImages(runner.BuildImagesKey{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
	})

	if err != nil {
		return false, err
	}

	image, err := buildImagesRes.GetLatestImage()

	if err != nil {
		return false, err
	}

	deployRequest := request.DeployBuild{
		ComponentId: g.params.CompDetails.Component.Id,
		VersionId:   latestApiVersion.Id,
		EnvId:       g.params.Environment.Id,
		ImageId:     image.ImageID,
	}

	deployResponse, err := api.DeployBuild(client, deployRequest)

	if err != nil {
		return false, err
	}

	if deployResponse.DeployDeploymentTrack != "Sucessfully deployed" {
		return false, errors.New("deployment failed")
	}

	return true, nil
}

func (g *deployBuild) Name() string {
	return "deployBuild"
}

func (g *deployBuild) WaitTill() int64 {
	return 0
}
