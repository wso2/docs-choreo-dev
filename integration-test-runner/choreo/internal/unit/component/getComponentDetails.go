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

type getComponentDetails struct {
	params *GetComponentDetailsParams
	state  *runner.SpecState
}

type GetComponentDetailsParams struct {
	ProjectRes  *response.CreateProject
	Placeholder string
}

func GetComponentDetails(state *runner.SpecState, params *GetComponentDetailsParams) *getComponentDetails {
	return &getComponentDetails{
		params: params,
		state:  state,
	}
}

func (c *getComponentDetails) Execute(client *resty.Client) (unit.UnitComplete, error) {
	createCompRes, err := c.state.GetComponentResponse(c.params.Placeholder)

	if err != nil {
		return false, err
	}

	componentHandler := createCompRes.Handle
	if createCompRes.Handler != "" {
		componentHandler = createCompRes.Handler
	}

	req := request.GetComponentDetails{
		ProjectId:        c.params.ProjectRes.Project.Id,
		ComponentHandler: componentHandler,
	}

	createCompReq, err := c.state.GetComponentRequest(c.params.Placeholder)

	if err != nil {
		return false, err
	}

	expected := response.GetComponentDetails{
		Component: response.Component{
			Name:       createCompReq.Name,
			OrgId:      c.state.GetOrgId(),
			OrgHandler: c.state.GetOrgHandler(),
			ProjectId:  c.params.ProjectRes.Project.Id,
		}}

	res, err := api.GetComponentDetails(client, req, &expected)

	if err != nil {
		return false, err
	}

	c.state.SetComponentDetailsResponse(c.params.Placeholder, *res)

	return true, nil
}

func (c *getComponentDetails) Name() string {
	return "getComponentDetails"
}

func (c *getComponentDetails) WaitTill() int64 {
	return 0
}
