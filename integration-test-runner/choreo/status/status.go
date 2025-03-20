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

package status

type Status struct {
	isFailed    bool
	isRetryable bool
	rawResponse []byte
	errorStr    string
}

func NewSuccessStatus(response []byte) Status {
	return Status{
		rawResponse: response,
	}
}

func NewPermFailedStatus(errorStr string) Status {
	return Status{
		isFailed:    true,
		isRetryable: false,
		errorStr:    errorStr,
	}
}

func NewTempFailedStatus(errorStr string) Status {
	return Status{
		isFailed:    true,
		isRetryable: true,
		errorStr:    errorStr,
	}
}

func (s *Status) IsFailed() bool {
	return s.isFailed
}

func (s *Status) IsRetryable() bool {
	return s.isRetryable
}

func (s *Status) GetMessage() string {
	return s.errorStr
}

func (s *Status) GetRawResponse() []byte {
	return s.rawResponse
}
