/*
 * Copyright (c) 2023, WSO2 Inc. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.common;

import lombok.Getter;

public enum Buildpack {
    GOLANG("F9E4820E-6284-11EE-8C99-0242AC120005", "1.x"),
    PYTHON("F9E4820E-6284-11EE-8C99-0242AC120003", "3.10.x"),
    JAVA("F9E4820E-6284-11EE-8C99-0242AC120002", "11"),
    NODEJS("F9E4820E-6284-11EE-8C99-0242AC120004","16.x.x"),
    PHP("F9E4820E-6284-11EE-8C99-0242AC120006", "8.1.x"),
    RUBY("F9E4820E-6284-11EE-8C99-0242AC120007", "3.1.x"),
    PRISM_MOCK("91AE0ACC-5B59-EF11-BDFD-000D3A0F15BE", "");

    @Getter
    private String id;
    @Getter
    private String version;

    private Buildpack(String id, String version) {
        this.id = id;
        this.version = version;
    }
}
