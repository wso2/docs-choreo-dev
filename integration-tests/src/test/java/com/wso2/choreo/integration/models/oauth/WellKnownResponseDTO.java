/*
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
 */

package com.wso2.choreo.integration.models.oauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WellKnownResponseDTO {
    @NonNull
    private String issuer;
    @NonNull
    private String authorization_endpoint;
    @NonNull
    private String token_endpoint;
    @NonNull
    private String[] token_endpoint_auth_methods_supported;
    @NonNull
    private String jwks_uri;
    @NonNull
    private String end_session_endpoint;
    @NonNull
    private String[] response_types_supported;
    @NonNull
    private String[] grant_types_supported;
    @NonNull
    private String revocation_endpoint;
    @NonNull
    private String[] revocation_endpoint_auth_methods_supported;
    @NonNull
    private String introspection_endpoint;
    @NonNull
    private String[] introspection_endpoint_auth_methods_supported;
    @NonNull
    private String[] code_challenge_methods_supported;
    @NonNull
    private String[] subject_types_supported;
    @NonNull
    private String[] id_token_signing_alg_values_supported;
    @NonNull
    private String[] scopes_supported;
}
