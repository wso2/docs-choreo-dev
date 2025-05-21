package com.wso2.choreo.integration.models.commithistory;


import com.wso2.choreo.integration.common.exceptions.NoLatestCommitHashFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Commit {

    private  String message;
    private String sha;
    private boolean isLatest;
    private Author author;



    public  static Commit getLatestCommit(List<Commit> commits) throws NoLatestCommitHashFoundException {
        for (Commit commit:
             commits) {
            if (commit.isLatest()) {
                return commit;
            }
        }
      throw new NoLatestCommitHashFoundException();
    }
}
