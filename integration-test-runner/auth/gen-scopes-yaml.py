#!/usr/bin/python3

#   Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
#
#   This software is the property of WSO2 LLC. and its suppliers, if any.
#   Dissemination of any information or reproduction of any material contained
#   herein is strictly forbidden, unless permitted by WSO2 in accordance with
#   the WSO2 Commercial License available at http://wso2.com/licenses.
#   For specific language governing the permissions and limitations under
#   this license, please see the license as well as any agreement you’ve
#   entered into with WSO2 governing the purchase of this software and any
#   associated services.

from argparse import ArgumentParser
import jwt

# Scope yaml printer
def printScopesToYaml(scopeList, scopeFile):
    f = open(scopeFile, "w")
    f.write("scopes:\n")
    for scope in scopeList:
        f.write("  - " + scope + "\n")
    f.close()

parser = ArgumentParser()
parser.add_argument("-t", "--token", help="the oauth token", required=True)
parser.add_argument("-e", "--env", help="the Choreo environment you want to generate the scopes for", choices=["dev","stage","prod"], required=True)

args = parser.parse_args()

args.token = args.token.replace("Bearer ", "")

decoded = jwt.decode(args.token, verify=False)

issuer = decoded["iss"]
scopes = decoded["scope"]

# Validate token issuer against the environment provided
if args.env == "dev":
    if issuer.count("sts.preview-dv.choreo.dev") == 0:
        print("error: token is not issued for dev environment")
        exit(1)
    else:
        printScopesToYaml(scopes.split(), "dev-scopes.yaml")
elif args.env == "stage":
    if issuer.count("sts.st.choreo.dev") == 0:
        print("error: token is not issued for stage environment")
        exit(1)
    else:
        printScopesToYaml(scopes.split(), "stage-scopes.yaml")
elif args.env == "prod":
    if issuer.count("sts.choreo.dev") == 0:
        print("error: token is not issued for prod environment")
        exit(1)
    else:
        printScopesToYaml(scopes.split(), "prod-scopes.yaml")
else:
    print("error: token does not match with any known environment")
    exit(1)




