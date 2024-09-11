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

import sys
from devops.pipeline_reader import PipelineReader
from persist.big_query_writer import BigQueryWriter
from persist.data import IntegrationTest

def capture_test_results(env):
    pipeline = PipelineReader(env)

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

    bq_writer = BigQueryWriter()
    bq_writer.insert_integration_test_data(values)

if __name__ == '__main__':
    if len(sys.argv) != 2:
        print("Choreo env not specified as argument. Required env - dev|stage|prod should be provided")
        sys.exit(1)

    env = sys.argv[1]
        
    capture_test_results(env)
