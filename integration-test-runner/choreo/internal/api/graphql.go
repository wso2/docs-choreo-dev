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
	req "choreo-integration-test-runner/model/request"
	res "choreo-integration-test-runner/model/response"
	"choreo-integration-test-runner/template"
	"net/http"

	"github.com/go-resty/resty/v2"
)

func CreateProject(client *resty.Client, model req.CreateProject) (*res.CreateProject, status.Status) {
	buf, err := template.PopulateRequestTemplate("createProject", model)
	if err != nil {
		return nil, status.NewPermFailedStatus(err.Error())
	}

	var response map[string]res.CreateProject

	request := make(map[string]string)
	request["query"] = buf.String()

	res, err := client.R().
		SetBody(request).
		SetResult(&response).
		Post(graphql)

	if err != nil {
		return nil, status.NewPermFailedStatus(err.Error())
	}

	if res.StatusCode() != http.StatusOK {
		return nil, status.NewTempFailedStatus(res.Status())
	}

	data := response["data"]

	return &data, status.NewSuccessStatus()
}

func CreateComponent(client *resty.Client, model req.CreateComponent) (*res.CreateComponent, []byte, status.Status) {
	buf, err := template.PopulateRequestTemplate("createComponent", model)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	var response map[string]res.CreateComponent

	request := make(map[string]string)
	request["query"] = buf.String()

	res, err := client.R().
		SetBody(request).
		SetResult(&response).
		Post(graphql)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	if res.StatusCode() != http.StatusOK {
		return nil, res.Body(), status.NewTempFailedStatus(res.Status())
	}

	data := response["data"]

	return &data, res.Body(), status.NewSuccessStatus()
}

func GetComponentDetails(client *resty.Client, model req.GetComponentDetails) (*res.GetComponentDetails, []byte, status.Status) {
	buf, err := template.PopulateRequestTemplate("getComponentDetails", model)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	var response map[string]res.GetComponentDetails

	request := make(map[string]string)
	request["query"] = buf.String()

	res, err := client.R().
		SetBody(request).
		SetResult(&response).
		Post(graphql)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	if res.StatusCode() != http.StatusOK {
		return nil, res.Body(), status.NewTempFailedStatus(res.Status())
	}

	data := response["data"]

	return &data, res.Body(), status.NewSuccessStatus()
}

func GetCommitHistory(client *resty.Client, model req.GetCommitHistory) (*res.GetCommitHistory, []byte, status.Status) {
	buf, err := template.PopulateRequestTemplate("getCommitHistory", model)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	var response map[string]res.GetCommitHistory

	request := make(map[string]string)
	request["query"] = buf.String()

	res, err := client.R().
		SetBody(request).
		SetResult(&response).
		Post(graphql)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	if res.StatusCode() != http.StatusOK {
		return nil, res.Body(), status.NewTempFailedStatus(res.Status())
	}

	data := response["data"]

	return &data, res.Body(), status.NewSuccessStatus()

}

func GetDeploymentEnvironments(client *resty.Client, model req.GetDeploymentEnvironments) (*res.GetDeploymentEnvironments, []byte, status.Status) {
	buf, err := template.PopulateRequestTemplate("getDeploymentEnvironments", model)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	var response map[string]res.GetDeploymentEnvironments

	request := make(map[string]string)
	request["query"] = buf.String()

	res, err := client.R().
		SetBody(request).
		SetResult(&response).
		Post(graphql)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	if res.StatusCode() != http.StatusOK {
		return nil, res.Body(), status.NewTempFailedStatus(res.Status())
	}

	data := response["data"]

	return &data, res.Body(), status.NewSuccessStatus()
}

func GetDeploymentStatusByVersion(client *resty.Client, model req.GetDeploymentStatusByVersion) (*res.GetDeploymentStatusByVersion, []byte, status.Status) {
	buf, err := template.PopulateRequestTemplate("getDeploymentStatusByVersion", model)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	var response map[string]res.GetDeploymentStatusByVersion

	request := make(map[string]string)
	request["query"] = buf.String()

	res, err := client.R().
		SetBody(request).
		SetResult(&response).
		Post(graphql)

	if err != nil {
		return nil, []byte{}, status.NewPermFailedStatus(err.Error())
	}

	if res.StatusCode() != http.StatusOK {
		return nil, res.Body(), status.NewTempFailedStatus(res.Status())
	}

	data := response["data"]

	return &data, res.Body(), status.NewSuccessStatus()
}
