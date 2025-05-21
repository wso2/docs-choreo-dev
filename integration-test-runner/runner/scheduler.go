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
	"container/list"
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
	tokenApi     *auth.TokenApi
	concurrency  int
	pendingSpecs *list.List
	successSpecs map[string][]specRuntimeData
	errorSpecs   []specRuntimeData
	activeSpecs  map[string]specRuntimeData
	waitingSpecs map[string]specRuntimeData
	execDone     chan string
	mu           sync.Mutex
	ticker       *time.Ticker
}

func NewScheduler(specs []*Spec, concurrency int) (*scheduler, error) {
	sch := &scheduler{
		execDone:     make(chan string),
		pendingSpecs: list.New(),
		errorSpecs:   make([]specRuntimeData, 0, int(float64(len(specs))*0.25)),
		successSpecs: make(map[string][]specRuntimeData),
		activeSpecs:  make(map[string]specRuntimeData),
		waitingSpecs: make(map[string]specRuntimeData),
		concurrency:  concurrency,
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
		sch.pendingSpecs.PushBack(specRuntimeData{
			Spec:   spec,
			logger: logger.NewTestLogger(spec.name, fmt.Sprintf("./%s.log", spec.name)),
			Ctx:    buildContext(spec, orgHolder),
		})
	}

	return sch, nil
}

func buildContext(spec *Spec, orgHolder *OrgHolder) context.Context {
	state := NewState(orgHolder, len(spec.actions))

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
			s.evaluateWaitingSpecs()
			if s.endRunOnCompletion() {
				return
			}
		}
	}
}

func (s *scheduler) SuccessSpecs() []specRuntimeData {
	data := make([]specRuntimeData, 0)

	for _, specs := range s.successSpecs {
		data = append(data, specs...)
	}

	return data
}

func (s *scheduler) addToSuccessSpecs(data specRuntimeData) {
	coll, ok := s.successSpecs[data.Spec.kind]

	if !ok {
		coll = make([]specRuntimeData, 0)
	}

	coll = append(coll, data)

	s.successSpecs[data.Spec.kind] = coll
}

func (s *scheduler) FailedSpecs() []specRuntimeData {
	return s.errorSpecs
}

func (s *scheduler) runPendingSpecs() {
	if s.pendingSpecs.Len() == 0 {
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

	for e := s.pendingSpecs.Front(); e != nil; {
		if len(s.activeSpecs) > s.concurrency {
			break
		}

		data := e.Value.(specRuntimeData)

		if data.Spec.extends != "" {
			finished, ok := s.successSpecs[data.Spec.extends]

			if !ok { // No successful specs that this spec extends
				e = e.Next()
				continue
			}

			// Copy over ctx of a successful spec, in this case we pick the first one but any of them would do
			data.Ctx = finished[0].Ctx
		}

		s.activeSpecs[data.Spec.name] = data
		client := newClient(token, data.logger)
		go data.Spec.Execute(data.Ctx, client, data.logger, s.execDone)

		next := e.Next()
		s.pendingSpecs.Remove(e)
		e = next
	}
}

func (s *scheduler) endRunOnCompletion() bool {
	s.mu.Lock()
	defer s.mu.Unlock()

	var end bool

	if s.pendingSpecs.Len() == 0 && len(s.activeSpecs) == 0 && len(s.waitingSpecs) == 0 {
		fmt.Println("All specs completed")
		fmt.Println("Success specs:", len(s.successSpecs))
		fmt.Println("Error specs:", len(s.errorSpecs))
		fmt.Println("Ending run")
		s.ticker.Stop()
		end = true
	}

	return end
}

func (s *scheduler) evaluateWaitingSpecs() {
	s.mu.Lock()
	defer s.mu.Unlock()

	if len(s.waitingSpecs) == 0 {
		return
	}

	token, err := s.tokenApi.GetToken()

	if err != nil {
		fmt.Println("Error running pending specs", err)
		return
	}

	errorSpecNames := make([]string, 0)
	restartedSpecNames := make([]string, 0)

	for name, data := range s.waitingSpecs {
		state := appstate.GetState(data.Ctx).(*SpecState)

		if state.waitCount < 10 {
			if state.waitTill < time.Now().Unix() {
				data.logger.Debugf("Resuming from action index: %d, resume attempt: %d", state.nextSequenceIndex, state.waitCount)
				client := newClient(token, data.logger)
				go data.Spec.Execute(data.Ctx, client, data.logger, s.execDone)
				restartedSpecNames = append(restartedSpecNames, name)
			}
		} else {
			data.logger.Errorf("Waiting for too long, action index: %d, resume attempt: %d", state.nextSequenceIndex, state.waitCount)
			state.runResult = Error
			state.unrecoverableError = fmt.Errorf("waiting for too long")
			appstate.SetState(data.Ctx, state)
			errorSpecNames = append(errorSpecNames, name)
		}

	}

	for _, name := range errorSpecNames {
		if data, ok := s.waitingSpecs[name]; ok {
			s.errorSpecs = append(s.errorSpecs, data)
			delete(s.waitingSpecs, name)
		}
	}

	for _, name := range restartedSpecNames {
		if data, ok := s.waitingSpecs[name]; ok {
			s.activeSpecs[name] = data
			delete(s.waitingSpecs, name)
		}
	}
}

func (s *scheduler) processExecuted(name string) {
	s.mu.Lock()
	defer s.mu.Unlock()
	data := s.activeSpecs[name]

	state := appstate.GetState(data.Ctx).(*SpecState)

	switch state.runResult {
	case Successful:
		s.addToSuccessSpecs(data)
		delete(s.activeSpecs, name)
	case Error:
		s.errorSpecs = append(s.errorSpecs, data)
		actionState := state.GetActionState(state.nextSequenceIndex)
		run, err := actionState.GetLatestRun()

		if err != nil {
			data.logger.Errorf("Failed to get latest run: %v", err)
		} else {
			data.logger.Errorf("Failed reason for action index %d: %s", state.nextSequenceIndex, run.Reason)
		}
		delete(s.activeSpecs, name)
	case Waiting:
		actionState := state.GetActionState(state.nextSequenceIndex)
		run, err := actionState.GetLatestRun()

		if err != nil {
			data.logger.Errorf("Failed to get latest run: %v", err)
		} else {
			data.logger.Debugf("Waiting reason for action index %d: %s", state.nextSequenceIndex, run.Reason)
		}
		s.waitingSpecs[name] = data
		delete(s.activeSpecs, name)
	default:
		data.logger.Errorf("Unhandled result %d for action index %d\n", state.runResult, state.nextSequenceIndex)
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
	client.SetRetryMaxWaitTime(30 * time.Second)
	client.AddRetryCondition(
		func(r *resty.Response, err error) bool {
			return err != nil || r.StatusCode() >= 401
		},
	)

	return client
}
