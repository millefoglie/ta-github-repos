package com.github.millefoglie.tagithubrepos.service;

import com.github.millefoglie.tagithubrepos.client.GithubClient;
import com.github.millefoglie.tagithubrepos.exception.GithubException;
import com.github.millefoglie.tagithubrepos.exception.NotFoundException;
import com.github.millefoglie.tagithubrepos.mapper.GithubMapper;
import com.github.millefoglie.tagithubrepos.model.GetAllPublicReposResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GithubService {
    private static final Pattern OPTIONAL_PATH_FRAGMENT = Pattern.compile("\\{/.+\\}$");

    private final GithubClient githubClient;
    private final GithubMapper githubMapper;

    public Mono<GetAllPublicReposResponse> getAllPublicRepos(String username) {
        var nonForkRepoFlux = githubClient
                .getAllPublicRepos(username)
                .flatMapMany(Flux::fromIterable)
                .filter(it -> !it.isFork())
                .onErrorMap(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value())) {
                        return new NotFoundException("No " + username + " repositories found: " + e.getResponseBodyAsString());
                    }

                    return e;
                });

        var repoBranchesFlux = nonForkRepoFlux
                .flatMapSequential(repo -> {

                    // Strip {/branch} optional segment from the url
                    // For some reason UriComponentBuilder does not cover this case
                    var matcher = OPTIONAL_PATH_FRAGMENT.matcher(repo.getBranchesUrl());
                    var branchesUrl = matcher.replaceAll("");

                    return githubClient
                            .getRepoBranches(
                                    UriComponentsBuilder.fromHttpUrl(branchesUrl).encode().build().toUri()
                            )
                            .onErrorMap(WebClientResponseException.class, e -> {
                                if (e.getStatusCode() == HttpStatusCode.valueOf(HttpStatus.NOT_FOUND.value())) {
                                    return new NotFoundException(
                                            "No " + username + "/" + repo.getName() + " repository found: " + e.getResponseBodyAsString());
                                }

                                return e;
                            });
                });

        // Note: branches must be mapped sequentially for this zip to work correctly
        return nonForkRepoFlux
                .zipWith(repoBranchesFlux)
                .map(repoAndBranches -> {
                    var repo = githubMapper.toApiModel(repoAndBranches.getT1());
                    var branches = repoAndBranches.getT2().stream().map(githubMapper::toApiModel).toList();
                    return repo.branches(branches);
                })
                .collectList()
                .map(it -> new GetAllPublicReposResponse().repos(it))
                .onErrorMap(
                        WebClientResponseException.class,
                        e -> new GithubException("Could not fetch data from GitHub: " + e.getResponseBodyAsString())
                );
    }
}
