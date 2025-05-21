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

package auth

import (
	"choreo-integration-test-runner/config"
	"context"
	"fmt"
	"strings"

	"github.com/go-resty/resty/v2"
	"golang.org/x/oauth2"
)

const tokenExchangeGrantType = "urn:ietf:params:oauth:grant-type:token-exchange"
const subjectTokenType = "urn:ietf:params:oauth:token-type:jwt"
const requestedTokenType = "urn:ietf:params:oauth:token-type:jwt"

type TokenApi struct {
	username              string
	password              string
	choreoStsClientId     string
	choreoStsClientSecret string
	choreoStsEndpoint     string
	scopes                []string
	asgardeoConf          *oauth2.Config
	asgardeoToken         *oauth2.Token
	stsToken              TokenResponse
}

type TokenResponse struct {
	AccessToken     string `json:"access_token"`
	IssuedTokenType string `json:"issued_token_type"`
	Scope           string `json:"scope"`
	TokenType       string `json:"token_type"`
	ExpiresIn       int    `json:"expires_in"`
}

type AsgardeoConfig struct {
	ClientID     string
	ClientSecret string
	Endpoint     string
	Username     string
	Password     string
}

type ChoreoStsConfig struct {
	ClientID     string
	ClientSecret string
	Endpoint     string
	Scopes       []string
}

func NewTokenApi(asgardeoConfig *AsgardeoConfig, choreoStsConfig *ChoreoStsConfig) *TokenApi {
	return &TokenApi{
		username:              asgardeoConfig.Username,
		password:              asgardeoConfig.Password,
		choreoStsClientId:     choreoStsConfig.ClientID,
		choreoStsClientSecret: choreoStsConfig.ClientSecret,
		choreoStsEndpoint:     choreoStsConfig.Endpoint,
		scopes:                choreoStsConfig.Scopes,

		asgardeoConf: &oauth2.Config{
			ClientID:     asgardeoConfig.ClientID,
			ClientSecret: asgardeoConfig.ClientSecret,
			Endpoint: oauth2.Endpoint{
				AuthURL:  asgardeoConfig.Endpoint + "/oauth2/authorize",
				TokenURL: asgardeoConfig.Endpoint + "/oauth2/token",
			},
		},
	}
}

func (t *TokenApi) GetToken() (string, error) {
	isTokenFresh, err := t.generateAsgardeoToken()
	if err != nil {
		return "", err
	}

	orgHandle, err := config.GetConfig(config.TEST_CHOREO_ORG_HANDLE)

	if err != nil {
		return "", err
	}

	if isTokenFresh {
		err = t.getChoreoStsToken(orgHandle)
		if err != nil {
			return "", err
		}
	}

	return t.stsToken.AccessToken, nil
}

func (t *TokenApi) generateAsgardeoToken() (bool, error) {
	ctx := context.Background()

	var err error

	if t.asgardeoToken == nil {
		t.asgardeoToken, err = t.asgardeoConf.PasswordCredentialsToken(ctx, t.username, t.password)

		return true, err // Initial token call, therefore token is fresh providing there are no errors
	}

	var isTokenFresh bool

	// If token is invalid a new token will be returned if there are no errors
	if !t.asgardeoToken.Valid() {
		isTokenFresh = true
	}

	t.asgardeoToken, err = t.asgardeoConf.TokenSource(ctx, t.asgardeoToken).Token()

	return isTokenFresh, err
}

func (t *TokenApi) getChoreoStsToken(orgHandle string) error {
	client := resty.New()

	client.SetBasicAuth(t.choreoStsClientId, t.choreoStsClientSecret)

	resp, err := client.R().
		SetFormData(map[string]string{
			"grant_type":           tokenExchangeGrantType,
			"subject_token":        t.asgardeoToken.AccessToken,
			"subject_token_type":   subjectTokenType,
			"requested_token_type": requestedTokenType,
			"orgHandle":            orgHandle,
			"scope":                t.buildScopesString(),
			"clientId":             t.choreoStsClientId,
		}).
		SetResult(&t.stsToken).
		Post(t.choreoStsEndpoint + "/oauth2/token")

	if err != nil {
		return err
	}

	if resp.StatusCode() != 200 {
		return fmt.Errorf("failed to get Choreo STS token: %s : {%s}", resp.Status(), resp.Body())
	}

	return nil
}

func (t *TokenApi) buildScopesString() string {
	var sb strings.Builder

	for i, scope := range t.scopes {
		sb.WriteString(scope)

		if i != len(t.scopes)-1 {
			sb.WriteString(" ")
		}
	}

	return sb.String()
}
