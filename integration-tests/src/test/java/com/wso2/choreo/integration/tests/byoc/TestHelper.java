package com.wso2.choreo.integration.tests.byoc;

import com.wso2.choreo.integration.common.utils.HttpClientUtil;
import com.wso2.choreo.integration.common.utils.ObjectMapperUtil;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.Response;
import com.wso2.choreo.integration.models.testconfigs.TestConfigs;

public class TestHelper {
    public static Movie[] getMovies(TestConfigs configs, Constant.Environment environment) {
        String apiInvocationRequestURI = configs.getConfig(environment.name()).getInvokeUrl() + "/movies";
        Response response = HttpClientUtil.httpGET(apiInvocationRequestURI, "", configs.getConfig(Constant.Environment.Development.name()).getApiKey());
        return ObjectMapperUtil.mapToCollection(Movie[].class, response.getRes(), "");
    }

    static class Movie {
        int id;
        String name;
        double ratings;
    }
}
