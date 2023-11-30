package com.github.millefoglie.tagithubrepos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GithubRepositoryResponse {
    long id;
    String name;
    Owner owner;
    boolean fork;

    @JsonProperty("branches_url")
    String branchesUrl;

    @Getter
    @Setter
    public static class Owner {
        String login;
    }
}
