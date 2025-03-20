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
	"choreo-integration-test-runner/auth"
	"choreo-integration-test-runner/config"
	"choreo-integration-test-runner/helper/matcher"
	"choreo-integration-test-runner/logger"
	"context"
	"fmt"
	"strconv"
	"strings"

	"github.com/go-resty/resty/v2"
)

type runnerKey string

const (
	stateKey        runnerKey = "state"
	clientKey       runnerKey = "client"
	tokenHandlerKey runnerKey = "tokenHandler"
)

type Spec struct {
	ctx     context.Context
	actions []Action
}

func newClient(accessToken string, l resty.Logger) *resty.Client {
	client := resty.New()

	client.SetAuthToken(accessToken)
	client.SetDebug(true)
	client.SetHeader("Content-Type", "application/json")
	client.SetLogger(l)

	return client
}

func (r *Spec) Init(name string, actions []Action) error {
	asgardeoConfig, err := getAsgardeoConfig()

	if err != nil {
		return err
	}

	choreoStsConfig, err := getChoreoStsConfig()

	if err != nil {
		return err
	}

	tokenApi := auth.NewTokenApi(asgardeoConfig, choreoStsConfig)

	l := logger.NewTestLogger(name, fmt.Sprintf("./%s.log", name))

	token, err := tokenApi.GetToken()

	if err != nil {
		return err
	}

	client := newClient(token, l)

	orgHolder, err := getOrgHolder()

	if err != nil {
		return err
	}

	r.actions = actions

	sequences := make([]int, len(actions))

	for _, action := range actions {
		sequences = append(sequences, action.GetSequence())
	}

	state := NewState(orgHolder, sequences)

	ctx := context.Background()
	ctx = context.WithValue(ctx, stateKey, state)
	ctx = context.WithValue(ctx, clientKey, client)
	ctx = context.WithValue(ctx, tokenHandlerKey, tokenApi)
	r.ctx = ctx

	return nil
}

func (s *Spec) Execute() {
	state := s.GetState()

	for _, action := range s.actions {
		sequence := action.GetSequence()
		params := action.GetParams()

		actionState, ok := state.GetActionState(sequence)

		if !ok {
			state.unrecoverableError = fmt.Errorf("function state not found for sequence: %d", sequence)
			break
		}

		err := action.SanitizeParams(params)

		if err != nil {
			state.unrecoverableError = err
			break
		}

		client := s.getClient()

		result := action.Execute(client, state, actionState, params)

		if result.IsValidateResponse {
			validateResponse(actionState, result.Response, action.GetResponseGenerator())
		}

		if actionState.Runs[len(actionState.Runs)-1].RunState != Success {
			break
		} else {
			s.handleSubActions(client, state, action, actionState, params)
		}
	}

	s.setState(state)

}

func (s *Spec) handleSubActions(client *resty.Client, state *SpecState, action Action, actionState *ActionState, params map[string]string) {
	subAction := action.GetSubAction()

	for subAction != nil {
		result := subAction.Execute(client, state, actionState, params)

		if result.IsValidateResponse {
			validateResponse(actionState, result.Response, subAction.GetResponseGenerator())
		}

		if actionState.Runs[len(actionState.Runs)-1].RunState != Success {
			break
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

	scopes, err := auth.GetScopes(values[config.TOKEN_SCOPES])

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

func getOrgHolder() (*OrgHolder, error) {
	var orgHandle string
	var orgIdStr string
	var orgUuid string
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

	if orgUuid, err = config.GetConfig(config.TEST_CHOREO_ORG_UUID); err != nil {
		return nil, err
	}

	return &OrgHolder{
		OrgHandler: orgHandle,
		OrgId:      orgId,
		OrgUuid:    orgUuid,
	}, nil
}

func (s *Spec) GetState() *SpecState {
	return s.ctx.Value(stateKey).(*SpecState)
}

func (s *Spec) setState(state *SpecState) context.Context {
	return context.WithValue(s.ctx, stateKey, state)
}

func (s *Spec) getClient() *resty.Client {
	return s.ctx.Value(clientKey).(*resty.Client)
}
