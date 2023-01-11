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

export namespace Enums {

    export enum DisplayType {
        restAPI = "restAPI",
        proxy = "proxy",
        webhook = "webhook",
        graphql = "graphql"
    }

    export enum ComponentTemplate {
        REST = "REST",
        WEBHOOK = "WEBHOOK",
        MANUAL = "MANUAL",
    }


    export enum DocumentSourceType {
        INLINE = 'INLINE',
        MARKDOWN = 'MARKDOWN',
        URL = 'URL',
        FILE = 'FILE',
    }


    export enum DocumentType {
        SAMPLE = 'SAMPLES',
        HOWTO = 'HOWTO',
        PUBLIC_FORUM = 'PUBLIC_FORUM',
        SUPPORT_FORUM = 'SUPPORT_FORUM',
        OTHER = 'OTHER',
    }

    export enum Environment {
        DEVELOPMENT = "Development",
        PRODUCTION = "Production",
        STAGING = "Staging",
    }


    export enum HTTPMethod {
        GET = 'GET',
        POST = 'POST',
        DELETE = 'DELETE',
        PUT = 'PUT',
        HEAD = 'HEAD',
        CONNECT = 'CONNECT',
        TRACE = 'TRACE',
    }

    export enum ConnectorAudience {
        PUBLIC = 'public',
        PRIVATE = 'private',
    }

    export enum Region {
        EU = "EU",
        US = "US"
    }
}