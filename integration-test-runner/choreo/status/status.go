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
	message     string
}

func NewSuccessStatus() Status {
	return Status{}
}

func NewPermFailedStatus(message string) Status {
	return Status{
		isFailed:    true,
		isRetryable: false,
		message:     message,
	}
}

func NewTempFailedStatus(message string) Status {
	return Status{
		isFailed:    true,
		isRetryable: true,
		message:     message,
	}
}

func (s *Status) IsFailed() bool {
	return s.isFailed
}

func (s *Status) IsRetryable() bool {
	return s.isRetryable
}

func (s *Status) GetMessage() string {
	return s.message
}
