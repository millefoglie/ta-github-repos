package com.github.millefoglie.tagithubrepos.api;

import com.github.millefoglie.tagithubrepos.model.RepositoriesData;
import com.github.millefoglie.tagithubrepos.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class DefaultRepositoryApiDelegate implements RepositoryApiDelegate {
    private final GithubService githubService;

    @Override
    public Mono<ResponseEntity<RepositoriesData>> getAllPublicRepos(String username, ServerWebExchange exchange) {
        return githubService
                .getAllPublicRepos(username)
                .map(ResponseEntity::ok);
    }
}
