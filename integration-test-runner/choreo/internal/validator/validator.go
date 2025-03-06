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

package validator

import (
	"bytes"
	"choreo-integration-test-runner/choreo"
	"choreo-integration-test-runner/helper/matcher"
	"choreo-integration-test-runner/runner"
	"context"
	"fmt"
	"strings"
)

type FunctionChecker interface {
	Sanitize(state *runner.State, params map[string]string) error
}

type ResponseTemplateHandler interface {
	ReadExpectedResponse() (*bytes.Buffer, error)
}

func PreExecutionSetup(ctx context.Context, sequence int, params map[string]string, funcChecker FunctionChecker) (context.Context, bool) {
	state := choreo.GetState(ctx)

	_, ok := state.FunctionStates[sequence]

	if !ok {
		state.UnrecoverableError = fmt.Errorf("function state not found for sequence: %d", sequence)
	} else {
		err := funcChecker.Sanitize(state, params)

		if err != nil {
			state.UnrecoverableError = err
			ok = false
		}
	}

	return choreo.SetState(ctx, state), ok
}

func ValidateResponse(state *runner.State, funcState *[]runner.Run, actualResponse []byte, templateHandler ResponseTemplateHandler) bool {
	expectedResponse, err := templateHandler.ReadExpectedResponse()

	if err != nil {
		*funcState = append(*funcState, runner.Run{
			RunState: runner.Skipped,
			Reason:   err.Error(),
		})
		return false
	}

	result, err := matcher.JsonMatch(expectedResponse.Bytes(), actualResponse)
	if err != nil {
		*funcState = append(*funcState, runner.Run{
			RunState: runner.Skipped,
			Reason:   err.Error(),
		})
		return false
	}

	if !result.Match {
		*funcState = append(*funcState, runner.Run{
			RunState: runner.Failed,
			Reason:   strings.Join(result.ErrorMsgs, ", "),
		})
		return false
	}

	*funcState = append(*funcState, runner.Run{
		RunState: runner.Success,
	})

	return true
}
