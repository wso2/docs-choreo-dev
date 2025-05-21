#!/bin/bash

#
# Copyright © 2025 WSO2 LLC. (http://www.wso2.com).
#
# This software is the property of WSO2 LLC and its suppliers, if any.
# Dissemination of any information or reproduction of any material contained
# herein is strictly forbidden, unless permitted by WSO2 in accordance with
# the WSO2 Commercial License available at http://wso2.com/licenses.
# For specific language governing the permissions and limitations under
# this license, please see the license as well as any agreement you’ve
# entered into with WSO2 governing the purchase of this software and any
# associated services.
#

# This script is used to run unit and integration tests to verify functionality of the test runner

export RUNNER_INTEGRATION="true"

go test -v ./...
