package com.wso2.choreo.integration.models.revision;

import java.io.IOException;

import com.wso2.choreo.integration.common.APICreator;
import com.wso2.choreo.integration.common.choreoproject.ChoreoComponent;
import com.wso2.choreo.integration.models.graphql.ApiRevisionDTO;
import com.wso2.choreo.integration.models.proxyapi.DeploySettings;

public class RevisionDeploymentRequest {
    public static DeploySettings deployRevision(ChoreoComponent choreoComponent, ApiRevisionDTO apiRevisionDTO, String envId, String accessToken) throws IOException {
        DeploySettings res = APICreator.deployRevision(choreoComponent.getId(), 
                apiRevisionDTO.getVersionId(), 
                envId, 
                apiRevisionDTO.getOrgUuid(),
                apiRevisionDTO.getOldRevisionId(), 
                apiRevisionDTO.getBuildId(), 
                apiRevisionDTO.getApiId(), 
                accessToken);
        return res;
    }
}
