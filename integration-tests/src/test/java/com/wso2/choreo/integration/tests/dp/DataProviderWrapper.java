package com.wso2.choreo.integration.tests.dp;

import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.common.choreoproject.ChoreoProject;
import com.wso2.choreo.integration.config.Constant;
import com.wso2.choreo.integration.models.apimanager.KeyData;
import com.wso2.choreo.integration.models.environments.Environment;
import com.wso2.choreo.integration.models.graphql.ComponentDeploymentStatusDTO;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPI;
import com.wso2.choreo.integration.models.proxyapi.ProxyAPIBuild;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DataProviderWrapper {


    private ChoreoComponent choreoComponent;
    private ChoreoProject choreoProject;
    private KeyData keyData;
    private String apiId;
    private Constant.Environment dev = Constant.Environment.Development;
    private Constant.Environment prd = Constant.Environment.Production;
    private String firstName;
    private String context;
    private ProxyAPI proxyAPI;
    private String secondName;
    private Environment devEnv;
    private Environment prdEnv;
    private ProxyAPIBuild proxyAPIBuild;
    private String devInvokeUrl;
    private String prodInvokeUrl;
    private String apiKey;
    private String region;
    // To store the buildId for RestAPI test cases
    private String buildId;
    private List<Environment> environments;
    List<ComponentDeploymentStatusDTO> promoteStatusDTO;


    public static <T> Object[][] convertToDataProvider(List<T> list) {
        Object[][] dataProvider = new Object[list.size()][1];
        for (int i = 0; i < list.size(); i++) {
            dataProvider[i][0] = list.get(i);
        }
        return dataProvider;
    }


}
