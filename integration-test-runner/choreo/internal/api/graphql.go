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

func CreateProject(client *resty.Client, request req.CreateProject) (*res.CreateProject, error) {
	return callGraphQL[req.CreateProject, res.CreateProject](client, "createProject", request, nil)
}

func DeleteProject(client *resty.Client, request req.DeleteProject, expected *res.DeleteProject) (*res.DeleteProject, error) {
	return callGraphQL[req.DeleteProject, res.DeleteProject](client, "deleteProject", request, nil)
}

func CreateComponent(client *resty.Client, request req.CreateComponent, expected *res.CreateComponent) (*res.CreateComponent, error) {
	return callGraphQL(client, "createComponent", request, expected)
}

func CreateByocComponent(client *resty.Client, request req.CreateByocComponent, expected *res.CreateByocComponent) (*res.CreateByocComponent, error) {
	return callGraphQL(client, "createByocComponent", request, expected)
}

func GetComponentDetails(client *resty.Client, request req.GetComponentDetails, expected *res.GetComponentDetails) (*res.GetComponentDetails, error) {
	return callGraphQL(client, "getComponentDetails", request, expected)
}

func GetCommitHistoryByBranch(client *resty.Client, request req.GetCommitHistoryByBranch) (*res.GetCommitHistory, error) {
	return callGraphQL[req.GetCommitHistoryByBranch, res.GetCommitHistory](client, "getCommitHistoryByBranch", request, nil)
}

func GetDeploymentEnvironments(client *resty.Client, request req.GetDeploymentEnvironments) (*res.GetDeploymentEnvironments, error) {
	return callGraphQL[req.GetDeploymentEnvironments, res.GetDeploymentEnvironments](client, "getDeploymentEnvironments", request, nil)
}

func GetDeploymentStatusByVersion(client *resty.Client, request req.GetDeploymentStatusByVersion) (*res.GetDeploymentStatusByVersion, error) {
	return callGraphQL[req.GetDeploymentStatusByVersion, res.GetDeploymentStatusByVersion](client, "getDeploymentStatusByVersion", request, nil)
}

func DeployComponent(client *resty.Client, request req.DeployComponent) (*res.DeployComponent, error) {
	return callGraphQL[req.DeployComponent, res.DeployComponent](client, "deployComponent", request, nil)
}

func GetBuildImages(client *resty.Client, request req.GetBuildImages) (*res.GetBuildImages, error) {
	return callGraphQL[req.GetBuildImages, res.GetBuildImages](client, "getBuildImages", request, nil)
}

func DeployBuild(client *resty.Client, request req.DeployBuild) (*res.DeployBuild, error) {
	return callGraphQL[req.DeployBuild, res.DeployBuild](client, "deployBuild", request, nil)
}

func GetEndpoints(client *resty.Client, request req.GetEndpoints) (*res.GetEndpoints, error) {
	return callGraphQL[req.GetEndpoints, res.GetEndpoints](client, "getEndpoints", request, nil)
}

func GenerateEndpoints(client *resty.Client, request req.GenerateEndpoints) (*res.GenerateEndpoints, error) {
	return callGraphQL[req.GenerateEndpoints, res.GenerateEndpoints](client, "generateEndpoints", request, nil)
}

func PromoteEndpoints(client *resty.Client, request req.PromoteEndpoints) (*res.PromoteEndpoints, error) {
	return callGraphQL[req.PromoteEndpoints, res.PromoteEndpoints](client, "promoteEndpoints", request, nil)
}

func UpdateEndpoint(client *resty.Client, request req.UpdateEndpoint) (*res.UpdateEndpoint, error) {
	return callGraphQL[req.UpdateEndpoint, res.UpdateEndpoint](client, "updateEndpoint", request, nil)
}

func GetComponentDeployment(client *resty.Client, request req.GetComponentDeployment) (*res.GetComponentDeployment, error) {
	return callGraphQL[req.GetComponentDeployment, res.GetComponentDeployment](client, "getComponentDeployment", request, nil)
}

func PromoteComponent(client *resty.Client, request req.PromoteComponent) (*res.PromoteComponent, error) {
	return callGraphQL[req.PromoteComponent, res.PromoteComponent](client, "promoteComponent", request, nil)
}

func callGraphQL[Req_t, Res_t any](client *resty.Client, templateName string, request Req_t, expected *Res_t) (*Res_t, error) {
	buf, err := template.PopulateRequestTemplate(templateName, request)

	if err != nil {
		return nil, err
	}

	newAppServiceHost, err := config.GetConfig(config.CHOREO_NEW_APP_SERVICE_ENDPOINT)

	if err != nil {
		return nil, err
	}

	var response map[string]Res_t

	body := make(map[string]string)
	body["query"] = buf.String()

	res, err := client.R().
		SetBody(body).
		SetResult(&response).
		Post(newAppServiceHost + graphql)

	if err != nil {
		return nil, err
	}

	if res.StatusCode() != http.StatusOK {
		return nil, errors.New("graphql call failed, response code: " + res.Status())
	}

	if expected != nil {
		err = validateResponse(templateName, res.Body(), expected)

		if err != nil {
			return nil, err
		}
	}

	data := response["data"]

	return &data, nil
}

func validateResponse[Res_t any](templateName string, response []byte, expected *Res_t) error {
	expectedResponse, err := template.PopulateResponseTemplate(templateName, expected)

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
