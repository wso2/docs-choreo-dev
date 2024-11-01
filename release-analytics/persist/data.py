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

from dataclasses import dataclass
from datetime import datetime

@dataclass
class IntegrationTest:
    build_number: str
    pipeline_start_time: datetime
    commit_msg: str
    test_type: str
    test_function: str
    outcome: str
    error_msg: str
    stack_trace: str
    choreo_env: str

    def get_as_row(self):
        return {
            "build_number": self.build_number,
            "pipeline_start_time": self.pipeline_start_time.strftime("%Y-%m-%dT%H:%M:%S"),
            "commit_msg": self.commit_msg,
            "test_type": self.test_type,
            "test_function": self.test_function,
            "outcome": self.outcome,
            "error_msg": self.error_msg,
            "stack_trace": self.stack_trace,
            "choreo_env": self.choreo_env
        }

@dataclass
class ReleasePromotion:
    build_number: str
    commit_msg: str
    promotion_time: datetime
    source_env: str
    dest_env: str
    release_id: str

    def get_as_row(self):
        return {
            "build_number": self.build_number,
            "commit_msg": self.commit_msg,
            "promotion_time": self.promotion_time.strftime("%Y-%m-%dT%H:%M:%S"),
            "source_env": self.source_env,
            "dest_env": self.dest_env,
            "release_id": self.release_id
        }

@dataclass
class DeployedComponent:
    build_number: str
    choreo_env: str
    component_name: str
    image: str
    time_stamp: datetime
    tag: str

    def get_as_row(self):
        return {
            "build_number": self.build_number,
            "choreo_env": self.choreo_env,
            "component_name": self.component_name,
            "image": self.image,
            "time_stamp": self.time_stamp.strftime("%Y-%m-%dT%H:%M:%S"),
            "tag": self.tag
        }
