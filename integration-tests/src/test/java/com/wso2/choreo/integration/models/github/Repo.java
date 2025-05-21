package com.wso2.choreo.integration.models.github;


import lombok.Data;

import java.util.Objects;

@Data
public class Repo {

    private long id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repo repo = (Repo) o;
        return id == repo.id && Objects.equals(name, repo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
