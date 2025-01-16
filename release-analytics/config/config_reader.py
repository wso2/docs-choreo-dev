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
from enum import Enum
import yaml


class ConfigGroup(Enum):
    DEVOPS = 1
    BIGQUERY = 2


def get_gcloud_account_info():
    try:
        gcloud_var: str = os.environ['RELEASE_ANALYTICS_GCLOUD_ACCOUNT_INFO']
        return json.loads(base64.b64decode(gcloud_var).decode("utf-8"))
    except KeyError:
        print("You must first set the RELEASE_ANALYTICS_GCLOUD_ACCOUNT_INFO environment variable")
        sys.exit(1)
    except binascii.Error:
        print("Error when decoding GCloud credentials")
        sys.exit(1)

def get_azure_devops_pat():
    try:
        pat: str = os.environ['RELEASE_ANALYTICS_AZURE_DEVOPS_PAT']
        print("Length of PAT: ", len(pat))
        pat = pat.strip()
        print("Length of PAT after strip: ", len(pat))
        return pat
    except KeyError:
        print("You must first set the RELEASE_ANALYTICS_AZURE_DEVOPS_PAT environment variable")
        sys.exit(1)



class ConfigReader(object):
    config_group_mapping = {
        ConfigGroup.DEVOPS: "devops",
        ConfigGroup.BIGQUERY: "bigquery"
    }

    def __init__(self):
        with open('config/config.yaml', 'r') as file:
            self.config = yaml.safe_load(file)

    def __new__(cls):
        if not hasattr(cls, 'instance'):
            cls.instance = super(ConfigReader, cls).__new__(cls)
        return cls.instance

    def get_config(self, group: ConfigGroup, key: str):
        return self.config[self.config_group_mapping[group]][key]
