"""
 * Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
"""

#!/usr/bin/python3

import sys
from datetime import datetime, timedelta, timezone
from azure.devops.credentials import BasicAuthentication
from azure.devops.connection import Connection
from config.config_reader import ConfigGroup, ConfigReader


class PipelineReader:
    url = ConfigReader().get_config(ConfigGroup.DEVOPS, "url")
    user_agent = ConfigReader().get_config(ConfigGroup.DEVOPS, "user-agent")
    max_test_results = ConfigReader().get_config(ConfigGroup.DEVOPS, "max-test-results")

    def __init__(self, project_name, definition_name):
        self.conn = None
        self.project = project_name
        self.definition = definition_name

    def create_connection(self, auth_token):
        self.conn = Connection(base_url=PipelineReader.url, 
                    creds=BasicAuthentication('', auth_token),
                    user_agent=PipelineReader.user_agent)

    def get_test_results(self):
        test_results = {}

        latest_build = self._get_latest_pipeline_build()

        change = self._get_build_change(latest_build.id)

        test_results["build_number"] = latest_build.build_number
        test_results["start_time"] = latest_build.start_time
        test_results["commit_msg"] = change.message

        runs = self._get_test_runs_by_build(latest_build.id)

        tests = []

        for run in runs:
            results = self._get_test_results_by_run(run.id)

            for result in results:
                #print("=============================================\n")
                #print("Returned Test Result {} \n".format(result))
                #print("=============================================\n")
                tests.append(dict(test_type = run.name, 
                                    test_function = result.test_case.name,
                                    outcome = result.outcome,
                                    error_msg = result.error_message,
                                    stack_trace = result.stack_trace
                                    ))

        test_results["tests"] = tests

        return test_results

    def _get_latest_pipeline_build(self):
        build_client = self.conn.clients.get_build_client()

        def_obj = self._get_definition()

        try:
            latest_build = None
            for build in build_client.get_builds(self.project, definitions=[def_obj.id], 
                                            status_filter="inProgress", top=20):
                if latest_build is None or build.start_time > latest_build.start_time:
                    latest_build = build

            build = build_client.get_latest_build(self.project, def_obj.id)
            
            if build is not None:
                if latest_build is None or build.start_time > latest_build.start_time:
                    latest_build = build

            return latest_build
        except Exception as e:
            print("_get_latest_pipeline_build() raised error: {}".format(e))
            sys.exit(1)

    def _get_build_change(self, build_id):
        build_client = self.conn.clients.get_build_client()

        def_obj = self._get_definition()

        try:
            for change in build_client.get_build_changes(self.project, build_id):
                return change # There will only be one change
        except Exception as e:
            print("_get_build_change() raised error: {}".format(e))
            sys.exit(1)
        

    def _get_definition(self):
        build_client = self.conn.clients.get_build_client()

        try:
            for def_obj in build_client.get_definitions(self.project):
                if self.definition in def_obj.name:
                    return def_obj

        except Exception as e:
            print("_get_definition() raised error: {}".format(e))
            sys.exit(1)


    def _get_test_runs_by_build(self, build_id):
        client = self.conn.clients.get_test_client()
        
        end = datetime.now(timezone.utc)
        start = end - timedelta(days=6)

        try:
            runs = client.query_test_runs(self.project, start, end, build_ids=[build_id])
            
            if runs:
                print("{} Test runs found".format(len(runs)))
            else:
                print("Test runs NOT found")

            return runs

        except Exception as e:
            print("_get_test_runs_by_build() raised error: {}".format(e))
            sys.exit(1)


    def _get_test_results_by_run(self, run_id):
        client = self.conn.clients.get_test_client()

        try:
            results = client.get_test_results(self.project, run_id, 
                        top=PipelineReader.max_test_results, outcomes=["Failed", "NotExecuted", "Passed"])
            
            if results:
                print("{} Test results found".format(len(results)))
            else:
                print("Test results NOT found")

            return results

        except Exception as e:
            print("_get_failed_test_results_by_run() raised error: {}".format(e))
            sys.exit(1)


