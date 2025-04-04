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
	"bytes"

	"github.com/go-resty/resty/v2"
)

type ExecutionResult struct {
	IsValidateResponse bool
	IsWaiting          bool
	Response           []byte
}

type ResponseGenerator interface {
	GenExpectedResponse() (*bytes.Buffer, error)
}

type Action interface {
	GetParams() map[string]string
	GetSequence() int
	Execute(client *resty.Client, state *SpecState, actionState *ActionState, params map[string]string) ExecutionResult
	SanitizeParams(params map[string]string) error
	GetSubAction() SubAction
	GetResponseGenerator() ResponseGenerator
}

type SubAction interface {
	Execute(client *resty.Client, state *SpecState, actionState *ActionState, params map[string]string) ExecutionResult
	GetSubAction() SubAction
	GetResponseGenerator() ResponseGenerator
}
