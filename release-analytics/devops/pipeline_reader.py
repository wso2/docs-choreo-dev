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

import os
import sys
import requests
from datetime import datetime, timedelta, timezone
from tempfile import TemporaryFile
from zipfile import ZipFile
from azure.devops.credentials import BasicAuthentication
from azure.devops.connection import Connection
from config.config_reader import ConfigGroup, ConfigReader, get_azure_devops_pat

COMPONENT_ARTIFACT = "output"
COMPONENT_DIRECTORY = "component-info"
CP_COMPONENT_CSV = "cp-component-info.csv"
DP_COMPONENT_CSV = "dp-component-info.csv"

class PipelineReader:
    url = ConfigReader().get_config(ConfigGroup.DEVOPS, "url")
    user_agent = ConfigReader().get_config(ConfigGroup.DEVOPS, "user-agent")
    max_test_results = ConfigReader().get_config(ConfigGroup.DEVOPS, "max-test-results")

    def __init__(self, env):
        if env == "dev":
            self.project = ConfigReader().get_config(ConfigGroup.DEVOPS, "dev-project")
            self.definition = ConfigReader().get_config(ConfigGroup.DEVOPS, "dev-definition")
        elif env == "stage":
            self.project = ConfigReader().get_config(ConfigGroup.DEVOPS, "stage-project")
            self.definition = ConfigReader().get_config(ConfigGroup.DEVOPS, "stage-definition")
        elif env == "prod":
            self.project = ConfigReader().get_config(ConfigGroup.DEVOPS, "prod-project")
            self.definition = ConfigReader().get_config(ConfigGroup.DEVOPS, "prod-definition")
        else:
            print(f"Unrecognized env: {env} specified")
            sys.exit(1)

        auth_token = get_azure_devops_pat()

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
                tests.append(dict(test_type = self._map_test_type(run),
                                  test_function = result.test_case.name,
                                  outcome = result.outcome,
                                  error_msg = result.error_message,
                                  stack_trace = result.stack_trace
                                  ))

        test_results["tests"] = tests

        return test_results

    def get_promotion_data(self, build_id, src_env, release_id):
        dest_env = "stage"
        if src_env not in ["dev", "stage"]:
            print("Invalid source environment provided")
            sys.exit(1)
        else:
            if src_env == "stage":
                dest_env = "prod"

        promotion_data = {}
        build = self._get_build_by_id(build_id)

        change = self._get_build_change(build.id)

        promotion_data["build_number"] = build.build_number
        promotion_data["commit_msg"] = change.message
        promotion_data["promotion_time"] = datetime.now(timezone.utc)
        promotion_data["source_env"] = src_env
        promotion_data["dest_env"] = dest_env
        promotion_data["release_id"] = release_id

        return promotion_data


    def get_build_number(self, build_id):
        build = self._get_build_by_id(build_id)
        return build.build_number


    def _get_build_by_id(self, build_id):
        build_client = self.conn.clients.get_build_client()

        def_obj = self._get_definition()

        try:
            for build in build_client.get_builds(self.project, build_ids=[build_id]):
                return build  # There will only a single match
        except Exception as e:
            print("_get_build_by_id() raised error: {}".format(e))
            sys.exit(1)

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

    def download_component_artifact(self, build_id):
        stream = self._get_component_filestream(build_id)

        try:
            with TemporaryFile() as f:
                for i in stream:
                    f.write(i)
                f.seek(0)
                zf = ZipFile(f)
                zf.extractall(path=".")
        except Exception as e:
            print("download_component_artifact() raised error: {}".format(e))
            sys.exit(1)

    def _get_component_filestream(self, build_id):
        client = self.conn.clients.get_build_client()

        try:
            stream = client.get_artifact_content_zip(self.project, build_id, artifact_name=COMPONENT_ARTIFACT)
            if stream:
                return stream
            else:
                print("Artifact NOT found")
                sys.exit(1)
        except Exception as e:
            if "TF400813" in e.message: # Handle https://github.com/microsoft/azure-devops-python-api/issues/316
                info = self._get_component_file_info(build_id)
                stream = requests.get(info.resource.download_url, auth=("", get_azure_devops_pat()))
                return stream
            else:
                print("get_component_file() raised error: {}".format(e))
                sys.exit(1)

    def _get_component_file_info(self, build_id):
        client = self.conn.clients.get_build_client()

        try:
            info = client.get_artifact(self.project, build_id, artifact_name=COMPONENT_ARTIFACT)
            if info:
                return info
            else:
                print("Artifact NOT found")
                sys.exit(1)
        except Exception as e:
            print("get_artifacts() raised error: {}".format(e))
            sys.exit(1)


    @staticmethod
    def _map_test_type(test_run):
        if "IntegrationTests" == test_run.name:
            return "int"
        elif "PDPIntegrationTests" == test_run.name:
            return "pdp"
        elif "SecurityIntegrationTests" in test_run.name:
            return "sec"
        else:
            print("_map_test_type() Unrecognized test type: {}".format(test_run.name))
            sys.exit(1)
