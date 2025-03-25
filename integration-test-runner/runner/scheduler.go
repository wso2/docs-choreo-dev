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
	"choreo-integration-test-runner/helper/appstate"
	"choreo-integration-test-runner/logger"
	"context"
	"fmt"
	"strconv"
	"sync"
	"time"

	"github.com/go-resty/resty/v2"
)

type specRuntimeData struct {
	Spec   *Spec
	logger *logger.TestLogger
	Ctx    context.Context
}

type scheduler struct {
	tokenApi       *auth.TokenApi
	pendingIndex   int
	concurrency    int
	pendingSpecs   []specRuntimeData
	completedSpecs []specRuntimeData
	activeSpecs    map[string]specRuntimeData
	execDone       chan string
	mu             sync.Mutex
	ticker         *time.Ticker
}

func NewScheduler(specs []*Spec, concurrency int) (*scheduler, error) {
	sch := &scheduler{
		execDone:       make(chan string),
		pendingSpecs:   make([]specRuntimeData, 0, len(specs)),
		completedSpecs: make([]specRuntimeData, 0, len(specs)),
		activeSpecs:    make(map[string]specRuntimeData),
		concurrency:    concurrency,
	}

	asgardeoConfig, err := getAsgardeoConfig()

	if err != nil {
		return nil, err
	}

	choreoStsConfig, err := getChoreoStsConfig()

	if err != nil {
		return nil, err
	}

	sch.tokenApi = auth.NewTokenApi(asgardeoConfig, choreoStsConfig)

	orgHolder, err := getOrgHolder()

	if err != nil {
		return nil, err
	}

	for _, spec := range specs {
		sch.pendingSpecs = append(sch.pendingSpecs, specRuntimeData{
			Spec:   spec,
			logger: logger.NewTestLogger(spec.name, fmt.Sprintf("./%s.log", spec.name)),
			Ctx:    buildContext(spec, orgHolder),
		})
	}

	return sch, nil
}

func buildContext(spec *Spec, orgHolder *OrgHolder) context.Context {
	sequences := make([]int, 0, len(spec.actions))

	for _, action := range spec.actions {
		sequences = append(sequences, action.GetSequence())
	}

	state := NewState(orgHolder, sequences)

	ctx := context.Background()
	ctx = appstate.SetState(ctx, state)

	return ctx
}

func (s *scheduler) Run() {
	s.ticker = time.NewTicker(5 * time.Second)

	for {
		select {
		case name := <-s.execDone:
			s.processExecuted(name)
		case <-s.ticker.C:
			s.runPendingSpecs()
			s.evaluateIncompleteSpecs()
			if s.endRunOnCompletion() {
				return
			}
		}
	}
}

func (s *scheduler) CompletedSpecs() []specRuntimeData {
	return s.completedSpecs
}

func (s *scheduler) runPendingSpecs() {
	if s.pendingIndex >= len(s.pendingSpecs) {
		return
	}

	if len(s.activeSpecs) > s.concurrency {
		return
	}

	s.mu.Lock()
	defer s.mu.Unlock()

	token, err := s.tokenApi.GetToken()

	if err != nil {
		fmt.Println("Error running pending specs", err)
		return
	}

	for ; s.pendingIndex < len(s.pendingSpecs); s.pendingIndex++ {
		if len(s.activeSpecs) > s.concurrency {
			break
		}

		data := s.pendingSpecs[s.pendingIndex]

		s.activeSpecs[data.Spec.name] = data
		client := newClient(token, data.logger)
		go data.Spec.Execute(data.Ctx, client, s.execDone)
	}
}

func (s *scheduler) endRunOnCompletion() bool {
	s.mu.Lock()
	defer s.mu.Unlock()

	var end bool

	if s.pendingIndex >= len(s.pendingSpecs) && len(s.activeSpecs) == 0 {
		fmt.Println("Ending run")
		s.ticker.Stop()
		end = true
	}

	return end
}

func (s *scheduler) evaluateIncompleteSpecs() {
	s.mu.Lock()
	defer s.mu.Unlock()

	if len(s.activeSpecs) == 0 {
		return
	}

	token, err := s.tokenApi.GetToken()

	if err != nil {
		fmt.Println("Error running pending specs", err)
		return
	}

	for _, data := range s.activeSpecs {
		state := appstate.GetState(data.Ctx).(*SpecState)

		switch state.runResult {
		case Waiting:
			if state.waitTill < time.Now().Unix() {
				client := newClient(token, data.logger)
				go data.Spec.Execute(data.Ctx, client, s.execDone)
			}
		case Error:
		}

	}
}

func (s *scheduler) processExecuted(name string) {
	s.mu.Lock()
	defer s.mu.Unlock()
	data := s.activeSpecs[name]

	state := appstate.GetState(data.Ctx).(*SpecState)

	if state.runResult == Complete {
		s.completedSpecs = append(s.completedSpecs, data)
		delete(s.activeSpecs, name)
	}
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

func newClient(accessToken string, l resty.Logger) *resty.Client {
	client := resty.New()

	client.SetAuthToken(accessToken)
	client.SetDebug(true)
	client.SetHeader("Content-Type", "application/json")
	client.SetLogger(l)
	client.SetRetryCount(4)
	client.SetRetryWaitTime(2 * time.Second)
	client.SetRetryMaxWaitTime(10 * time.Second)
	client.AddRetryCondition(
		func(r *resty.Response, err error) bool {
			return err != nil || r.StatusCode() >= 500
		},
	)

	return client
}
