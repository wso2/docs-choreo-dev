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

from enum import StrEnum

import yaml


class ConfigGroup(StrEnum):
    DEVOPS = "devops"
    BIGQUERY = "bigquery"


class ConfigReader(object):
    def __init__(self):
        with open('config/config.yaml', 'r') as file:
            self.config = yaml.safe_load(file)

    def __new__(cls):
        if not hasattr(cls, 'instance'):
            cls.instance = super(ConfigReader, cls).__new__(cls)
        return cls.instance

    def get_config(self, group: ConfigGroup, key: str):
        return self.config[group.value][key]
