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
import argparse
import os
import sys
from datetime import datetime, timezone
from zipfile import ZipFile
from devops.pipeline_reader import PipelineReader, COMPONENT_ARTIFACT, COMPONENT_DIRECTORY, CP_COMPONENT_CSV, \
    DP_COMPONENT_CSV
from persist.big_query_writer import BigQueryWriter
from persist.data import IntegrationTest, ReleasePromotion, DeployedComponent
from process.component_info_reader import extract_component_data


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

def capture_release_promotion(build_id, env, release_id):
    """
    Capture the release promotion from source environment to destination environment
    :param build_id: Pipeline build Id
    :param env: Current environment
    :param release_id: Release ID
    :param dest_env: Destination environment
    :return: None
    """
    pipeline = PipelineReader(env)

    promo_data = pipeline.get_promotion_data(build_id, env, release_id,)

    values = []

    promotion = ReleasePromotion(
        build_number=promo_data["build_number"],
        commit_msg=promo_data["commit_msg"],
        promotion_time=promo_data["promotion_time"],
        source_env=promo_data["source_env"],
        dest_env=promo_data["dest_env"],
        release_id=release_id
    )

    values.append(promotion.get_as_row())

    bq_writer = BigQueryWriter()
    bq_writer.insert_release_promotion_data(values)

def capture_deployed_component_info(build_id, env):
    pipeline = PipelineReader(env)

    time_stamp = datetime.now(timezone.utc)

    pipeline.download_component_artifact(build_id)

    _unzip_csv_files()

    cp_table = extract_component_data("./{}/{}".format(COMPONENT_ARTIFACT, CP_COMPONENT_CSV))

    build_number = pipeline.get_build_number(build_id)

    _insert_component_data(build_number, time_stamp, cp_table)

    dp_table = extract_component_data("./{}/{}".format(COMPONENT_ARTIFACT, DP_COMPONENT_CSV))

    _insert_component_data(build_number, time_stamp, dp_table)


def _unzip_csv_files():
    try:
        csv_zip_path = os.path.join(os.path.dirname(__file__),
                                    "{}/{}.zip".format(COMPONENT_ARTIFACT, COMPONENT_DIRECTORY))

        with ZipFile(csv_zip_path, 'r') as csv_zip:
            csv_zip.extractall(path="./{}".format(COMPONENT_ARTIFACT))
    except Exception as e:
        print("_unzip_csv_files() raised error: {}".format(e))
        sys.exit(1)

def _insert_component_data(build_number, time_stamp, table):
    values = []

    for row in table:
        deployed_component = DeployedComponent(
            build_number=build_number,
            choreo_env=row["environment_name"],
            component_name=row["component_name"],
            image=row["image"],
            time_stamp=time_stamp,
            tag=row["tag"]
        )

        values.append(deployed_component.get_as_row())

    bq_writer = BigQueryWriter()
    bq_writer.insert_deployed_component_data(values)



if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Release Analytics CLI")
    sub_parsers = parser.add_subparsers(help="Sub Commands", dest="sub_command")
    # Test parser
    test_parser = sub_parsers.add_parser("test", help="Capture test results")
    test_parser.add_argument("-e", "--env", choices=["dev", "stage", "prod"], help="Choreo env",

                             required=True)
    # Promotion parser
    promo_parser = sub_parsers.add_parser("promotion", help="Capture release promotion")
    promo_parser.add_argument("-e", "--env", choices=["dev", "stage"], help="Choreo env being promoted",
                              required=True)
    promo_parser.add_argument("-b", "--build_id", help="Pipeline build Id", required=True)
    promo_parser.add_argument("-r", "--release_id", help="Release ID", required=True)

    # Component parser
    comp_parser = sub_parsers.add_parser("component", help="Capture release promotion")
    comp_parser.add_argument("-e", "--env", choices=["dev", "stage", "prod"], help="Choreo env",
                              required=True)
    comp_parser.add_argument("-b", "--build_id", help="Pipeline build Id", required=True)
    comp_parser.add_argument("-r", "--release_id", help="Release ID", required=True)

    args = parser.parse_args()

    if args.sub_command == "promotion":
        capture_release_promotion(args.build_id, args.env, args.release_id)
    elif args.sub_command == "component":
        capture_deployed_component_info(args.build_id, args.env)
    elif args.sub_command == "test":
        capture_test_results(args.env)
    else:
        parser.print_help()
        exit(1)
