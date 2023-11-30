package com.github.millefoglie.tagithubrepos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubBranchResponse {
    String name;
    Commit commit;

    @Getter
    @Setter
    public static class Commit {
        String sha;
    }
}
