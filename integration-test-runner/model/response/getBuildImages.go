/*
 * Copyright © 2025 WSO2 LLC. (http://www.wso2.com).
 *
 * This software is the property of WSO2 LLC and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you've
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

package response

import "errors"

type GetBuildImages struct {
	DeploymentTrackImages []DeploymentTrackImage `json:"deploymentTrackImages"`
}

type DeploymentTrackImage struct {
	ImageID       string     `json:"imageId"`
	CreatedAt     string     `json:"createdAt"`
	UpdatedAt     string     `json:"updatedAt"`
	CommitHash    string     `json:"commitHash"`
	CommitMessage string     `json:"commitMessage"`
	Author        AuthorInfo `json:"author"`
	RunID         string     `json:"runId"`
	BuiltAt       string     `json:"builtAt"`
}

type AuthorInfo struct {
	Name      string `json:"name"`
	Email     string `json:"email"`
	Date      string `json:"date"`
	AvatarURL string `json:"avatarUrl"`
}

func (g *GetBuildImages) GetLatestImage() (*DeploymentTrackImage, error) {
	if len(g.DeploymentTrackImages) > 0 {
		return &g.DeploymentTrackImages[0], nil
	}

	return nil, errors.New("latest image not found")
}
