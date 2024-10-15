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

import unittest

from config.config_reader import get_gcloud_account_info, get_azure_devops_pat


class MyTestCase(unittest.TestCase):
    def test_get_gcloud_account_info(self):
        info = get_gcloud_account_info()
        self.assertIsNotNone(info)
        self.assertEqual(info["type"], "service_account")

    def test_get_azure_devops_pat(self):
        pat = get_azure_devops_pat()
        self.assertIsNotNone(pat)
        self.assertNotEqual(len(pat), 0)


if __name__ == '__main__':
    unittest.main()
