package com.wso2.choreo.integration.models.revision;

import lombok.Data;

import java.util.List;

@Data
public class RevisionWrapper {

    private  int count;
    private List<Revision> list;
}
