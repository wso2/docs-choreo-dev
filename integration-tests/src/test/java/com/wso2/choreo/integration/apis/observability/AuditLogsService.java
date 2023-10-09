/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package com.wso2.choreo.integration.apis.observability;

import com.consol.citrus.TestActionRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import com.wso2.choreo.integration.apis.ControlPlaneAPI;
import com.wso2.choreo.integration.common.MessageUtils;
import com.wso2.choreo.integration.config.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.consol.citrus.container.RepeatOnErrorUntilTrue.Builder.repeatOnError;
import static com.consol.citrus.http.actions.HttpActionBuilder.http;
import static com.consol.citrus.validation.json.JsonPathMessageValidationContext.Builder.jsonPath;
import static org.hamcrest.Matchers.*;

public class AuditLogsService extends ControlPlaneAPI {

	private static TimeRangeISO getTimeRangeISO() {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		OffsetDateTime currentDateTime = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
		String startTime = fmt.format(currentDateTime.minusMinutes(100));
		String endTime = fmt.format(currentDateTime);
		return TimeRangeISO.builder().startTime(startTime).endTime(endTime).build();
	}

	public static void verifyAuditLogs(TestActionRunner runner, HttpClient client, String accessToken)
			throws Exception {
		String orgUuid = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
		String resourceUrl = Constant.OBSERVABILITY_AUDIT_LOGS + "/orgs/" + orgUuid + "/audit-logs";
		TimeRangeISO timeRangeISO = getTimeRangeISO();
		String body = MessageUtils.generateJson(new HashMap<String, Object>() {
			{

				put("startTime", timeRangeISO.getStartTime());
				put("endTime", timeRangeISO.getEndTime());
				put("limit", 100);
				put("sort", "desc");
			}
		});

		Map<String, Object> validationMap = new HashMap<>();
		validationMap.put("$.list.size()", greaterThan(0));
		runner.$(repeatOnError()
				.until("i = 4")
				.index("i")
				.autoSleep(3000)
				.actions(
						http().client(client)
								.send()
								.post(resourceUrl)
								.message()
								.header(HttpHeaders.AUTHORIZATION, accessToken)
								.contentType(String.valueOf(MediaType.APPLICATION_JSON))
								.accept(String.valueOf(MediaType.APPLICATION_JSON))
								.body(body),
						http().client(client)
								.receive()
								.response(HttpStatus.CREATED)
								.message()
								.body(new ClassPathResource(
										"templates/observability/responses/auditLogsResponseSuccess.json"))
								.validate(jsonPath().expressions(validationMap))));
	}

	public static void verifyDataPlanes(TestActionRunner runner, HttpClient client, String accessToken)
			throws Exception {
		String orgIntId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_ID);
		String requestUrl = Constant.DEVOPS_CLUSTERS +
				"/dataplanes?org_id=" + orgIntId;

		runner.$(http()
				.client(client)
				.send()
				.get(requestUrl)
				.message()
				.header(HttpHeaders.ACCEPT, "*/*")
				.header(Constant.X_CLOUD_TYPE, "choreo")
				.header(HttpHeaders.AUTHORIZATION, accessToken));
		runner.$(http()
				.client(client)
				.receive()
				.response(HttpStatus.OK)
				.message()
				.type(MessageType.JSON));
	}

	public static void verifyCloudPlanes(TestActionRunner runner, HttpClient client, String accessToken)
			throws Exception {
		String orgIntId = Configuration.getConfig(ConfigDefinition.TEST_CHOREO_ORG_UUID);
		String requestUrl = Constant.DEVOPS_CLUSTERS +
				"/clouddataplanes?org_uuid=" + orgIntId;

		runner.$(http()
				.client(client)
				.send()
				.get(requestUrl)
				.message()
				.header(HttpHeaders.ACCEPT, "*/*")
				.header(Constant.X_CLOUD_TYPE, "choreo")
				.header(HttpHeaders.AUTHORIZATION, accessToken));
		runner.$(http()
				.client(client)
				.receive()
				.response(HttpStatus.OK)
				.message()
				.type(MessageType.JSON));
	}

}
