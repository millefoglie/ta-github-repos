package com.github.millefoglie.tagithubrepos.mapper;

import com.github.millefoglie.tagithubrepos.dto.GithubBranchResponse;
import com.github.millefoglie.tagithubrepos.dto.GithubRepositoryResponse;
import com.github.millefoglie.tagithubrepos.model.GetAllPublicReposResponseReposInner;
import com.github.millefoglie.tagithubrepos.model.GetAllPublicReposResponseReposInnerBranchesInner;
import org.springframework.stereotype.Component;

@Component
public class GithubMapper {

    public GetAllPublicReposResponseReposInnerBranchesInner toApiModel(GithubBranchResponse source) {
        return new GetAllPublicReposResponseReposInnerBranchesInner()
                .name(source.getName())
                .lastCommitSha(source.getCommit().getSha());
    }

    public GetAllPublicReposResponseReposInner toApiModel(GithubRepositoryResponse source) {
        return new GetAllPublicReposResponseReposInner()
                .name(source.getName())
                .owner(source.getOwner().getLogin());
    }
}
