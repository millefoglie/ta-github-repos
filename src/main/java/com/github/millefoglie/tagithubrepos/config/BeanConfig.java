package com.github.millefoglie.tagithubrepos.config;

import com.github.millefoglie.tagithubrepos.client.GithubClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class BeanConfig {

    @Bean
    GithubClient githubClient() {
        var client = WebClient
                .builder()
                .baseUrl("https://api.github.com/")
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
                .build();
        var adapter = WebClientAdapter.create(client);
        var factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(GithubClient.class);
    }
}
