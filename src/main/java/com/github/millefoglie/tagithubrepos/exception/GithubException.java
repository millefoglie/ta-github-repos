package com.github.millefoglie.tagithubrepos.exception;

public class GithubException extends RuntimeException{
    public GithubException(String message) {
        super(message);
    }
}
