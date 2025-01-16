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
import csv
import sys

field_order = ["Environment Name", "Component Name", "Image", "Tag"]


def extract_component_data(file_path):
    rows = []

    try:
        with open(file_path, 'r') as csvfile:
            # creating a csv reader object
            csvreader = csv.reader(csvfile)

            # extracting field names through first row
            fields = next(csvreader)

            print("Total no. of rows: %d" % csvreader.line_num)

            assert fields == field_order, "Fields do not match"

            # extracting each data row one by one
            for row in csvreader:
                rows.append(row)
    except Exception as e:
        print("extract_component_data() raised error when processing csv file: {}".format(e))
        sys.exit(1)

    table = []
    for row in rows:
        row_data = {}
        for i in range(len(row)):
            row_data[field_order[i].lower().replace(" ", "_")] = row[i]

        table.append(row_data)

    return table


class ComponentInfoReader:
    pass
