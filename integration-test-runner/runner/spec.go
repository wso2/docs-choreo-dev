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

package runner

import (
	"choreo-integration-test-runner/helper/appstate"
	"choreo-integration-test-runner/helper/matcher"
	"context"
	"fmt"
	"strings"

	"github.com/go-resty/resty/v2"
)

type Spec struct {
	name    string
	actions []Action
}

func NewSpec(name string, actions []Action) *Spec {
	return &Spec{
		name:    name,
		actions: actions,
	}
}

func (s *Spec) Name() string {
	return s.name
}

func (s *Spec) Execute(ctx context.Context, client *resty.Client, done chan string) {
	state := appstate.GetState(ctx).(*SpecState)

	isWaiting := false

	for state.nextSequenceIndex < state.totalSequences {
		action := s.actions[state.nextSequenceIndex]

		sequence := action.GetSequence()
		params := action.GetParams()

		actionState, ok := state.GetActionState(sequence)

		if !ok {
			state.runResult = Error
			state.unrecoverableError = fmt.Errorf("function state not found for sequence: %d", sequence)
			break
		}

		err := action.SanitizeParams(params)

		if err != nil {
			state.runResult = Error
			state.unrecoverableError = err
			break
		}

		result := action.Execute(client, state, actionState, params)

		if actionState.Runs[len(actionState.Runs)-1].RunState == Failed {
			state.runResult = Error
			break
		}

		if result.IsValidateResponse {
			validateResponse(actionState, result.Response, action.GetResponseGenerator())
		}

		s.handleSubActions(client, state, action, actionState, params)

		if actionState.Runs[len(actionState.Runs)-1].RunState == Success {
			state.nextSequenceIndex++
		}

		isWaiting = result.IsWaiting
		if result.IsWaiting {
			state.runResult = Waiting
			break
		}
	}

	if state.nextSequenceIndex == state.totalSequences {
		if isWaiting {
			state.runResult = SpecRunResult(Error)
			state.unrecoverableError = fmt.Errorf("final sequence: %d has been executed but spec is waiting", state.nextSequenceIndex-1)
			return
		}

		state.runResult = SpecRunResult(Complete)
	}

	appstate.SetState(ctx, state)
	done <- s.name
}

func (s *Spec) handleSubActions(client *resty.Client, state *SpecState, action Action, actionState *ActionState, params map[string]string) {
	subAction := action.GetSubAction()

	for subAction != nil {
		result := subAction.Execute(client, state, actionState, params)

		if actionState.Runs[len(actionState.Runs)-1].RunState == Failed {
			break
		}

		if result.IsValidateResponse {
			validateResponse(actionState, result.Response, subAction.GetResponseGenerator())
		}

		subAction = subAction.GetSubAction()
	}

}

func validateResponse(actionState *ActionState, response []byte, gen ResponseGenerator) {
	expectedResponse, err := gen.GenExpectedResponse()

	if err != nil {
		actionState.Runs = append(actionState.Runs, Run{
			RunState: Skipped,
			Reason:   err.Error(),
		})
		return
	}

	result, err := matcher.JsonMatch(expectedResponse.Bytes(), response)
	if err != nil {
		actionState.Runs = append(actionState.Runs, Run{
			RunState: Skipped,
			Reason:   err.Error(),
		})
		return
	}

	if !result.Match {
		actionState.Runs = append(actionState.Runs, Run{
			RunState: Failed,
			Reason:   strings.Join(result.ErrorMsgs, ", "),
		})
		return
	}

	actionState.Runs = append(actionState.Runs, Run{
		RunState: Success,
	})
}
