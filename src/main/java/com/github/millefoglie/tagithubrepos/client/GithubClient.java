package com.github.millefoglie.tagithubrepos.client;

import com.github.millefoglie.tagithubrepos.dto.GithubBranchResponse;
import com.github.millefoglie.tagithubrepos.dto.GithubRepositoryResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

public interface GithubClient {

    @GetExchange("/users/{username}/repos")
    Mono<List<GithubRepositoryResponse>> getAllPublicRepos(@PathVariable("username") String username);

    // Note that branchesUri comes in repos responses
    // Technically, we could specify a path instead
    @GetExchange
    Mono<List<GithubBranchResponse>> getRepoBranches(URI branchesUri);
}
