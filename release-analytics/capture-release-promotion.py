#  Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
#
#  This software is the property of WSO2 Inc. and its suppliers, if any.
#  Dissemination of any information or reproduction of any material contained
#  herein is strictly forbidden, unless permitted by WSO2 in accordance with
#  the WSO2 Commercial License available at http://wso2.com/licenses.
#  For specific language governing the permissions and limitations under
#  this license, please see the license as well as any agreement you’ve
#  entered into with WSO2 governing the purchase of this software and any
#  associated services.

import sys

from devops.pipeline_reader import PipelineReader
from persist.big_query_writer import BigQueryWriter
from persist.data import ReleasePromotion


def capture_release_promotion(build_id, env):
    """
    Capture the release promotion from source environment to destination environment
    :param build_id: Pipeline build Id
    :param env: Current environment
    :param dest_env: Destination environment
    :return: None
    """
    pipeline = PipelineReader(env)

    promo_data = pipeline.get_promotion_data(build_id, env)

    values = []

    promotion = ReleasePromotion(
        build_number=promo_data["build_number"],
        commit_msg=promo_data["commit_msg"],
        promotion_time=promo_data["promotion_time"],
        source_env=promo_data["source_env"],
        dest_env=promo_data["dest_env"]
    )

    values.append(promotion.get_as_row())

    bq_writer = BigQueryWriter()
    bq_writer.insert_release_promotion_data(values)


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Pipeline build Id & Choreo env not specified as arguments")
        sys.exit(1)

    build_id = sys.argv[1]
    env = sys.argv[2]

    if env not in ['dev', 'stage']:
        print("Invalid env provided. Required env - dev|stage should be provided")
        sys.exit(1)

    capture_release_promotion(build_id, env)
