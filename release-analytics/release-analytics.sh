#!/bin/bash

#
# Copyright (c) 2024, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
#
# This software is the property of WSO2 Inc. and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein is strictly forbidden, unless permitted by WSO2 in accordance with
# the WSO2 Commercial License available at http://wso2.com/licenses.
# For specific language governing the permissions and limitations under
# this license, please see the license as well as any agreement you’ve
# entered into with WSO2 governing the purchase of this software and any
# associated services.
#

# This script is used to capture the integration test results from the devops
# and upload them to BigQuery

# Install virtual env
echo "----------------------------------"
echo "Installing virtual env"
echo "----------------------------------"
python3 -m venv venv

# Activate virtual env
echo "----------------------------------"
echo "Activating virtual env"
echo "----------------------------------"
# shellcheck disable=SC1091
source venv/bin/activate

# Install dependencies
echo "----------------------------------"
echo "Installing dependencies"
echo "----------------------------------"
python -m pip install -r requirements.txt

# Capture integration test results
echo "----------------------------------"
echo "Capturing release analytics with arguments: " "$@"
echo "----------------------------------"
python release_analytics.py "$@"

echo "----------------------------------"
echo "Deactivate virtual env"
echo "----------------------------------"
# Deactivate virtual env
deactivate
