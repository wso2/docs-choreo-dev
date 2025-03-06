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

package project

import (
	"choreo-integration-test-runner/choreo"
	"choreo-integration-test-runner/choreo/internal/api"
	"choreo-integration-test-runner/choreo/internal/validator"
	"choreo-integration-test-runner/helper/name"
	req "choreo-integration-test-runner/model/request"
	"choreo-integration-test-runner/runner"
	"context"
	"errors"
	"fmt"

	"github.com/go-resty/resty/v2"
)

type Project struct {
	placeholder string
}

func (p *Project) CreateProject(ctx context.Context, sequence int, params map[string]string) {
	ctx, ok := validator.PreExecutionSetup(ctx, sequence, params, p)

	if !ok {
		return
	}

	client := choreo.GetClient(ctx)

	state := choreo.GetState(ctx)
	funcState := state.FunctionStates[sequence]

	p.createProject(client, state, &funcState, params)

	choreo.SetState(ctx, state)

	fmt.Printf("state Inside: %v\n", state)
}

func (p *Project) createProject(client *resty.Client, state *runner.State, funcState *[]runner.Run, params map[string]string) {
	orgId := state.OrgHolder.OrgId
	orgHandler := state.OrgHolder.OrgHandler

	name := name.NewName("autotest")

	req := req.CreateProject{
		Name:           name.NameWithSeparators(),
		Description:    params["description"],
		ProjectHandler: name.Name(),
		OrgId:          orgId,
		OrgHandler:     orgHandler,
		Region:         params["region"],
	}

	resp, status := api.CreateProject(client, req)

	fmt.Println("status: ", status)

	if status.IsFailed() {
		*funcState = append(*funcState, runner.Run{
			RunState: runner.Failed,
			Reason:   status.GetMessage(),
		})
	} else {
		state.ProjectHolder[p.placeholder] = *resp
		*funcState = append(*funcState, runner.Run{
			RunState: runner.Success,
		})
	}
}

func (p *Project) Sanitize(state *runner.State, params map[string]string) error {
	mandatoryFields := []string{"description", "region", "placeholder"}
	for _, field := range mandatoryFields {
		if _, ok := params[field]; !ok {
			return errors.New(field + " is required")
		}
	}

	p.placeholder = params["placeholder"]

	return nil
}
