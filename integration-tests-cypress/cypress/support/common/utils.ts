/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
 *
 * This software is the property of WSO2 Inc. and its suppliers, if any.
 * Dissemination of any information or reproduction of any material contained
 * herein is strictly forbidden, unless permitted by WSO2 in accordance with
 * the WSO2 Commercial License available at http://wso2.com/licenses.
 * For specific language governing the permissions and limitations under
 * this license, please see the license as well as any agreement you’ve
 * entered into with WSO2 governing the purchase of this software and any
 * associated services.
 */

export const appNamePrefix = 'a' + Date.now();
export const DAY = 86400000;
export const marketplaceText = 'marketplace';
export const integrationsText = 'integrations';
export const servicesText = 'services';
export const APIsText = 'apis';
export const devOpsText = 'devops';

/**
 * Create name for app.
 *
 * @returns true name for a new app
 */
export const generateAppName = (name: string) => {
    return appNamePrefix + "-" + name;
}
  
/**
 * Create name for api.
 *
 * @returns true name for a new api
 */
export const generateApiName = (name: string) => {
    return appNamePrefix + name;
}

export const normalizeText = (s: string) => {
    return s.replace(/\s+/g, '\u00a0')
}

export const isOldApp = (name: string) => {
    if (name.length > 13) {
        let timestamp = Number(name.substring(1,14));
        if (!isNaN(timestamp)) {
            let currentTime = Date.now();
            if ((currentTime - timestamp) < DAY*7) {
                return false;
            }
        }
    }
    return true;
}
