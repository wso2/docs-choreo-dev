package com.wso2.choreo.integration.models.commithistory;


import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Commit {

    private  String message;
    private String sha;
    private boolean isLatest;
    private Author author;



    public  static Commit getLatestCommit(Commit[] commits) throws NoLatestCommitHashFoundException {
      return Arrays.stream(commits).filter(Commit::isLatest).findFirst().orElseThrow(NoLatestCommitHashFoundException::new);
    }
}
