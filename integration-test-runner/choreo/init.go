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

package choreo

import (
	"choreo-integration-test-runner/choreo/auth"
	"choreo-integration-test-runner/choreo/internal/api/config"
	"choreo-integration-test-runner/choreo/internal/api/scope"
	"choreo-integration-test-runner/logger"
	"choreo-integration-test-runner/runner"
	"context"
	"strconv"

	"github.com/go-resty/resty/v2"
)

type runnerKey string

const (
	stateKey        runnerKey = "state"
	clientKey       runnerKey = "client"
	tokenHandlerKey runnerKey = "tokenHandler"
)

func NewClient(accessToken string, l resty.Logger) *resty.Client {
	client := resty.New()

	client.SetAuthToken(accessToken)
	client.SetDebug(true)
	client.SetHeader("Content-Type", "application/json")
	client.SetLogger(l)

	return client
}

func InitCtx(test string, sequences []int) (context.Context, error) {

	asgardeoConfig, err := getAsgardeoConfig()

	if err != nil {
		return nil, err
	}

	choreoStsConfig, err := getChoreoStsConfig()

	if err != nil {
		return nil, err
	}

	tokenApi := auth.NewTokenApi(asgardeoConfig, choreoStsConfig)

	l := logger.NewTestLogger(test, "./test.log")

	token, err := tokenApi.GetToken()

	if err != nil {
		return nil, err
	}

	client := NewClient(token, l)

	orgHolder, err := getOrgHolder()

	if err != nil {
		return nil, err
	}

	state := runner.NewState(orgHolder, sequences)

	ctx := context.Background()
	ctx = context.WithValue(ctx, stateKey, state)
	ctx = context.WithValue(ctx, clientKey, client)
	ctx = context.WithValue(ctx, tokenHandlerKey, tokenApi)

	return ctx, nil
}

func GetState(ctx context.Context) *runner.State {
	return ctx.Value(stateKey).(*runner.State)
}

func SetState(ctx context.Context, state *runner.State) context.Context {
	return context.WithValue(ctx, stateKey, state)
}

func GetClient(ctx context.Context) *resty.Client {
	return ctx.Value(clientKey).(*resty.Client)
}

func GetTokenHandler(ctx context.Context) *auth.TokenApi {
	return ctx.Value(tokenHandlerKey).(*auth.TokenApi)
}

func getAsgardeoConfig() (*auth.AsgardeoConfig, error) {
	values := make(map[config.Definition]string)

	defs := []config.Definition{
		config.ASGARDEO_CLIENT_ID,
		config.ASGARDEO_CLIENT_SECRET,
		config.ASGARDEO_ENDPOINT,
		config.TEST_USER_EMAIL,
		config.TEST_USER_PASSWORD,
	}

	for _, def := range defs {
		value, err := config.GetConfig(def)
		if err != nil {
			return nil, err
		}
		values[def] = value
	}

	return &auth.AsgardeoConfig{
		ClientID:     values[config.ASGARDEO_CLIENT_ID],
		ClientSecret: values[config.ASGARDEO_CLIENT_SECRET],
		Endpoint:     values[config.ASGARDEO_ENDPOINT],
		Username:     values[config.TEST_USER_EMAIL],
		Password:     values[config.TEST_USER_PASSWORD],
	}, nil
}

func getChoreoStsConfig() (*auth.ChoreoStsConfig, error) {
	values := make(map[config.Definition]string)

	defs := []config.Definition{
		config.CP_APP_CLIENT_ID,
		config.CP_APP_CLIENT_SECRET,
		config.STS_ENDPOINT,
		config.TOKEN_SCOPES,
	}

	for _, def := range defs {
		value, err := config.GetConfig(def)
		if err != nil {
			return nil, err
		}
		values[def] = value
	}

	scopes, err := scope.GetScopes(values[config.TOKEN_SCOPES])

	if err != nil {
		return nil, err
	}

	return &auth.ChoreoStsConfig{
		ClientID:     values[config.CP_APP_CLIENT_ID],
		ClientSecret: values[config.CP_APP_CLIENT_SECRET],
		Endpoint:     values[config.STS_ENDPOINT],
		Scopes:       scopes,
	}, nil
}

func getOrgHolder() (*runner.OrgHolder, error) {
	var orgHandle string
	var orgIdStr string
	var orgId int
	var err error

	if orgHandle, err = config.GetConfig(config.TEST_CHOREO_ORG_HANDLE); err != nil {
		return nil, err
	}

	if orgIdStr, err = config.GetConfig(config.TEST_CHOREO_ORG_ID); err != nil {
		return nil, err
	}

	if orgId, err = strconv.Atoi(orgIdStr); err != nil {
		return nil, err
	}

	return &runner.OrgHolder{
		OrgHandler: orgHandle,
		OrgId:      orgId,
	}, nil
}
