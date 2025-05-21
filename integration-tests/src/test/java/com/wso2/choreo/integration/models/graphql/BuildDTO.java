package com.wso2.choreo.integration.models.graphql;

import com.wso2.choreo.integration.models.commithistory.Commit;
import lombok.Data;

@Data
public class BuildDTO {
    private String buildId;
    private Commit commit;
}
