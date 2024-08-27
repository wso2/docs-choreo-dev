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

import base64
import binascii
import json
import os
import sys

from config.config_reader import ConfigReader, ConfigGroup
from devops.pipeline_reader import PipelineReader
from persist.big_query_writer import BigQueryWriter
from persist.data import IntegrationTest

def capture_test_results(env, project_name, definition_name):
    try:
        auth_token = os.environ['AZURE_DEVOPS_PAT']
    except KeyError:
        print("You must first set the AZURE_DEVOPS_PAT environment variable")
        sys.exit(1)

    pipeline = PipelineReader(project_name, definition_name)
    pipeline.create_connection(auth_token)

    results = pipeline.get_test_results()

    values = []

    for test in results["tests"]:
       result = IntegrationTest(
                    build_number=results["build_number"],
                    pipeline_start_time=results["start_time"],
                    commit_msg=results["commit_msg"],
                    test_type=test["test_type"],
                    test_function=test["test_function"],
                    outcome=test["outcome"],
                    error_msg=test["error_msg"],
                    stack_trace=test["stack_trace"],
                    choreo_env=env
                )

       values.append(result.get_as_row())

    try:
        account_info = json.loads(base64.b64decode(os.environ['GCLOUD_ACCOUNT_INFO']).decode("utf-8"))
    except binascii.Error:
        print("Error when decoding GCloud credentials")
        sys.exit(1)

    bq_writer = BigQueryWriter(account_info)
    bq_writer.insert_integration_test_data(values)

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Choreo env not specified as argument. Required env - dev|stage|prod should be provided")
        sys.exit(1)

    env = sys.argv[1]

    config = ConfigReader()
    
    project_name = ""
    definition_name = ""

    if env == "dev":
        project_name = config.get_config(ConfigGroup.DEVOPS, "dev-project")
        definition_name = config.get_config(ConfigGroup.DEVOPS, "dev-definition")
    elif env == "stage":
        project_name = config.get_config(ConfigGroup.DEVOPS, "stage-project")
        definition_name = config.get_config(ConfigGroup.DEVOPS, "stage-definition")
    elif env == "prod":
        project_name = config.get_config(ConfigGroup.DEVOPS, "prod-project")
        definition_name = config.get_config(ConfigGroup.DEVOPS, "prod-definition")
    else:
        print(f"Unrecognized env: {env} specified")
        sys.exit(1)
        
    capture_test_results(env, project_name, definition_name)
