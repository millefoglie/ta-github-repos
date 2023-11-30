package com.github.millefoglie.tagithubrepos.mapper;

import com.github.millefoglie.tagithubrepos.dto.GithubBranchResponse;
import com.github.millefoglie.tagithubrepos.dto.GithubRepositoryResponse;
import com.github.millefoglie.tagithubrepos.model.RepositoriesDataReposInner;
import com.github.millefoglie.tagithubrepos.model.RepositoriesDataReposInnerBranchesInner;
import org.springframework.stereotype.Component;

@Component
public class GithubMapper {

    public RepositoriesDataReposInnerBranchesInner toApiModel(GithubBranchResponse source) {
        return new RepositoriesDataReposInnerBranchesInner()
                .name(source.getName())
                .lastCommitSha(source.getCommit().getSha());
    }

    public RepositoriesDataReposInner toApiModel(GithubRepositoryResponse source) {
        return new RepositoriesDataReposInner()
                .name(source.getName())
                .owner(source.getOwner().getLogin());
    }
}
