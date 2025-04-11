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
	"bytes"
	"choreo-integration-test-runner/config"
	"choreo-integration-test-runner/helper/matcher"
	req "choreo-integration-test-runner/model/request"
	res "choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/template"
	"errors"
	"net/http"
	"strings"

	"github.com/go-resty/resty/v2"
)

type ResponseGenerator interface {
	GenExpectedResponse() (*bytes.Buffer, error)
}

func CreateProject(client *resty.Client, model req.CreateProject) (*res.CreateProject, error) {
	return callGraphQL[req.CreateProject, res.CreateProject](client, "createProject", model, nil)
}

func CreateComponent(client *resty.Client, model req.CreateComponent, gen ResponseGenerator) (*res.CreateComponent, error) {
	return callGraphQL[req.CreateComponent, res.CreateComponent](client, "createComponent", model, gen)
}

func GetComponentDetails(client *resty.Client, model req.GetComponentDetails, gen ResponseGenerator) (*res.GetComponentDetails, error) {
	return callGraphQL[req.GetComponentDetails, res.GetComponentDetails](client, "getComponentDetails", model, gen)
}

func GetCommitHistoryByBranch(client *resty.Client, model req.GetCommitHistoryByBranch) (*res.GetCommitHistory, error) {
	return callGraphQL[req.GetCommitHistoryByBranch, res.GetCommitHistory](client, "getCommitHistoryByBranch", model, nil)
}

func GetDeploymentEnvironments(client *resty.Client, model req.GetDeploymentEnvironments) (*res.GetDeploymentEnvironments, error) {
	return callGraphQL[req.GetDeploymentEnvironments, res.GetDeploymentEnvironments](client, "getDeploymentEnvironments", model, nil)
}

func GetDeploymentStatusByVersion(client *resty.Client, model req.GetDeploymentStatusByVersion) (*res.GetDeploymentStatusByVersion, error) {
	return callGraphQL[req.GetDeploymentStatusByVersion, res.GetDeploymentStatusByVersion](client, "getDeploymentStatusByVersion", model, nil)
}

func DeployComponent(client *resty.Client, model req.DeployComponent) (*res.DeployComponent, error) {
	return callGraphQL[req.DeployComponent, res.DeployComponent](client, "deployComponent", model, nil)
}

func GetBuildImages(client *resty.Client, model req.GetBuildImages) (*res.GetBuildImages, error) {
	return callGraphQL[req.GetBuildImages, res.GetBuildImages](client, "getBuildImages", model, nil)
}

func DeployBuild(client *resty.Client, model req.DeployBuild) (*res.DeployBuild, error) {
	return callGraphQL[req.DeployBuild, res.DeployBuild](client, "deployBuild", model, nil)
}

func GetEndpoints(client *resty.Client, model req.GetEndpoints) (*res.GetEndpoints, error) {
	return callGraphQL[req.GetEndpoints, res.GetEndpoints](client, "getEndpoints", model, nil)
}

func GenerateEndpoints(client *resty.Client, model req.GenerateEndpoints) (*res.GenerateEndpoints, error) {
	return callGraphQL[req.GenerateEndpoints, res.GenerateEndpoints](client, "generateEndpoints", model, nil)
}

func UpdateEndpoint(client *resty.Client, model req.UpdateEndpoint) (*res.UpdateEndpoint, error) {
	return callGraphQL[req.UpdateEndpoint, res.UpdateEndpoint](client, "updateEndpoint", model, nil)
}

func GetComponentDeployment(client *resty.Client, model req.GetComponentDeployment) (*res.GetComponentDeployment, error) {
	return callGraphQL[req.GetComponentDeployment, res.GetComponentDeployment](client, "getComponentDeployment", model, nil)
}

func PromoteComponent(client *resty.Client, model req.PromoteComponent) (*res.PromoteComponent, error) {
	return callGraphQL[req.PromoteComponent, res.PromoteComponent](client, "promoteComponent", model, nil)
}

func callGraphQL[Req_t, Res_t any](client *resty.Client, templateName string, model Req_t, gen ResponseGenerator) (*Res_t, error) {
	buf, err := template.PopulateRequestTemplate(templateName, model)

	if err != nil {
		return nil, err
	}

	newAppServiceHost, err := config.GetConfig(config.CHOREO_NEW_APP_SERVICE_ENDPOINT)

	if err != nil {
		return nil, err
	}

	var response map[string]Res_t

	request := make(map[string]string)
	request["query"] = buf.String()

	res, err := client.R().
		SetBody(request).
		SetResult(&response).
		Post(newAppServiceHost + graphql)

	if err != nil {
		return nil, err
	}

	if res.StatusCode() != http.StatusOK {
		return nil, errors.New("graphql call failed, response code: " + res.Status())
	}

	if gen != nil {
		err = validateResponse(res.Body(), gen)
		if err != nil {
			return nil, err
		}
	}

	data := response["data"]

	return &data, nil
}

func validateResponse(response []byte, gen ResponseGenerator) error {
	expectedResponse, err := gen.GenExpectedResponse()

	if err != nil {
		return err
	}

	result, err := matcher.JsonMatch(expectedResponse.Bytes(), response)
	if err != nil {
		return err
	}

	if !result.Match {
		return errors.New(strings.Join(result.ErrorMsgs, ", "))
	}

	return nil
}
