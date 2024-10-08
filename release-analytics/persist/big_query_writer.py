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
import base64
import binascii
import json
import os
import sys
from google.cloud import bigquery
from google.oauth2 import service_account
from config.config_reader import ConfigReader, ConfigGroup

class BigQueryWriter:
    scopes = ConfigReader().get_config(ConfigGroup.BIGQUERY, "scopes")
    project = ConfigReader().get_config(ConfigGroup.BIGQUERY, "project")
    dataset = ConfigReader().get_config(ConfigGroup.BIGQUERY, "dataset")

    def __init__(self):
        try:
            account_info = json.loads(base64.b64decode(os.environ['GCLOUD_ACCOUNT_INFO']).decode("utf-8"))
        except binascii.Error:
            print("Error when decoding GCloud credentials")
            sys.exit(1)

        credentials = service_account.Credentials.from_service_account_info(account_info, scopes=BigQueryWriter.scopes)
        self.client = bigquery.Client(project=BigQueryWriter.project, credentials=credentials)
        

    def insert_integration_test_data(self, rows):
        table = f"{BigQueryWriter.project}.{BigQueryWriter.dataset}.INTEGRATION_TEST_RESULT"
        
        self._insert_data(table, rows)


    def insert_release_promotion_data(self, rows):
        table = f"{BigQueryWriter.project}.{BigQueryWriter.dataset}.RELEASE_PROMOTION"

        self._insert_data(table, rows)

    def insert_deployed_component_data(self, rows):
        table = f"{BigQueryWriter.project}.{BigQueryWriter.dataset}.DEPLOYED_COMPONENT"

        self._insert_data(table, rows)

    def _insert_data(self, table, rows):
        if len(rows) == 0:
            print(f"No rows present to insert into {table}")
            sys.exit(1)

        print(f"Inserting {len(rows)} rows into {table}")

        errors = self.client.insert_rows_json(table, rows)

        # Check if any errors occurred
        if errors:
            print("Errors occurred while inserting rows: {}".format(errors))
        else:
            print("Rows inserted successfully.")
