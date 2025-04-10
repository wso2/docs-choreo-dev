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

package response

import "errors"

type Author struct {
	Name      string `json:"name"`
	Date      string `json:"date"`
	Email     string `json:"email"`
	AvatarURL string `json:"avatarUrl"`
}

type Commit struct {
	Author   Author `json:"author"`
	Message  string `json:"message"`
	SHA      string `json:"sha"`
	IsLatest bool   `json:"isLatest"`
}

type GetCommitHistory struct {
	CommitHistory []Commit `json:"commitHistory"`
}

func (c *GetCommitHistory) GetLatestCommit() (*Commit, error) {
	for _, commit := range c.CommitHistory {
		if commit.IsLatest {
			return &commit, nil
		}
	}

	return nil, errors.New("latest commit not found")
}
