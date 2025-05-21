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
	"choreo-integration-test-runner/runner"

	"github.com/go-resty/resty/v2"
)

type getCommitHistoryByBranch struct {
	params *GetCommitHistoryByBranchParams
	state  *runner.SpecState
}

type GetCommitHistoryByBranchParams struct {
	Placeholder string
}

func GetCommitHistoryByBranch(state *runner.SpecState, params *GetCommitHistoryByBranchParams) *getCommitHistoryByBranch {
	return &getCommitHistoryByBranch{
		params: params,
		state:  state,
	}
}

func (c *getCommitHistoryByBranch) Execute(client *resty.Client) (unit.UnitComplete, error) {
	detailsRes, err := c.state.GetComponentDetailsResponse(c.params.Placeholder)

	if err != nil {
		return false, err
	}

	historyRequest := request.GetCommitHistoryByBranch{
		ComponentId: detailsRes.Component.Id,
		Branch:      detailsRes.Component.Repository.BranchApp,
	}

	historyResponse, err := api.GetCommitHistoryByBranch(client, historyRequest)

	if err != nil {
		return false, err
	}

	c.state.SetCommitHistory(c.params.Placeholder, *historyResponse)

	return true, nil
}

func (c *getCommitHistoryByBranch) Name() string {
	return "GetCommitHistoryByBranch"
}

func (c *getCommitHistoryByBranch) WaitTill() int64 {
	return 0
}
