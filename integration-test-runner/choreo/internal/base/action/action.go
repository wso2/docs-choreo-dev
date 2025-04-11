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

package action

import (
	"choreo-integration-test-runner/choreo/internal/unit"
	"choreo-integration-test-runner/runner"
	"errors"

	"github.com/go-resty/resty/v2"
)

type BaseAction struct {
	nextUnitIndex int
	params        map[string]string
	units         []unit.Unit
}

func (b *BaseAction) NextUnitIndex() int {
	return b.nextUnitIndex
}

func (b *BaseAction) Execute(client *resty.Client, actionState *runner.ActionState) {
	if len(b.units) == 0 {
		actionState.StoreRun(runner.Run{
			RunState: runner.Failed,
			Reason:   "Action has no units to execute",
		})
	}

	for b.nextUnitIndex < len(b.units) {
		unit := b.units[b.nextUnitIndex]
		complete, err := unit.Execute(client)

		if err != nil {
			actionState.StoreRun(runner.Run{
				RunState: runner.Failed,
				Reason:   err.Error(),
			})
			break
		}

		if !complete {
			actionState.StoreRun(runner.Run{
				RunState: runner.Progressing,
				Reason:   "Unit " + unit.Name() + " is still in progress",
				WaitTill: unit.WaitTill(),
			})
			break
		}

		b.nextUnitIndex++
	}

	if b.nextUnitIndex == len(b.units) {
		actionState.StoreRun(runner.Run{
			RunState: runner.Success,
			Reason:   "All units executed successfully",
		})
	}
}

func (b *BaseAction) Append(unit unit.Unit) {
	b.units = append(b.units, unit)
}

func (b *BaseAction) ResetUnits() {
	b.units = nil
}

func (c *BaseAction) SetParams(params map[string]string, mandatoryFields []string) error {
	for _, field := range mandatoryFields {
		if _, ok := params[field]; !ok {
			return errors.New(field + " is required")
		}
	}

	c.params = params

	return nil
}

func (c *BaseAction) ParamValue(key string) string {
	return c.params[key]
}
