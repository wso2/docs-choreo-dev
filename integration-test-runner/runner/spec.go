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
	"choreo-integration-test-runner/logger"
	"context"

	"github.com/go-resty/resty/v2"
)

type Action interface {
	RunMode() RunMode
	Execute(client *resty.Client, actionState *ActionState)
	NextUnitIndex() int
	SetParams(params map[string]string, mandatoryFields []string) error
	MandatoryFields() []string
	Init(state *SpecState) error
	ResetUnits()
	Name() string
}

type Spec struct {
	name    string
	kind    string
	extends string
	actions []Action
}

func NewSpec(name string, kind string, actions []Action) *Spec {
	return &Spec{
		name:    name,
		kind:    kind,
		actions: actions,
	}
}

func NewExtendedSpec(name string, kind string, extends string, actions []Action) *Spec {
	return &Spec{
		name:    name,
		kind:    kind,
		extends: extends,
		actions: actions,
	}
}

func (s *Spec) Name() string {
	return s.name
}

func (s *Spec) Execute(ctx context.Context, client *resty.Client, log *logger.TestLogger, done chan string) {
	state := appstate.GetState(ctx).(*SpecState)

	for state.nextSequenceIndex < state.NumberOfActions() {
		action := s.actions[state.nextSequenceIndex]

		actionState := state.GetActionState(state.nextSequenceIndex)

		action.ResetUnits()

		action.Init(state)

		action.Execute(client, &actionState)

		state.SetActionState(state.nextSequenceIndex, actionState)

		run, err := actionState.GetLatestRun()

		if err != nil {
			state.runResult = Error
			state.unrecoverableError = err
			break
		}

		var exitLoop bool

		switch run.RunState {
		case Success:
			log.Debugf("Action: %s(%d) executed successfully", action.Name(), state.nextSequenceIndex)
			state.ResetWaitTill()
			state.runResult = Successful
			state.nextSequenceIndex++
		case Failed:
			log.Errorf("Action: %s(%d) failed, reason: %s", action.Name(), state.nextSequenceIndex, run.Reason)
			state.runResult = Error
			exitLoop = true
		case Progressing:
			log.Debugf("Action: %s(%d) is still in progress", action.Name(), state.nextSequenceIndex)
			state.runResult = Waiting
			state.SetWaitTill(run.WaitTill)
			exitLoop = true
		default:
			log.Errorf("Unhandled state %d, for Action: %s(%d)", run.RunState, action.Name(), state.nextSequenceIndex)
		}

		if exitLoop {
			break
		}
	}

	if state.nextSequenceIndex == state.NumberOfActions() {
		state.runResult = SpecRunResult(Successful)
	}

	appstate.SetState(ctx, state)
	done <- s.name
}
