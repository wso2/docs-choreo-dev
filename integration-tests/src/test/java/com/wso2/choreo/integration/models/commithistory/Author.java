package com.wso2.choreo.integration.models.commithistory;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Author {
    private String name;
    private String date;
    private String email;
    private String avatarUrl;
}
