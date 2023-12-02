package com.github.millefoglie.tagithubrepos.config;

import com.github.millefoglie.tagithubrepos.exception.GithubException;
import com.github.millefoglie.tagithubrepos.exception.NotFoundException;
import com.github.millefoglie.tagithubrepos.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@ControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<?>> handleNotFoundException(NotFoundException e) {
        return handleException(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(GithubException.class)
    public Mono<ResponseEntity<?>> handleGithubException(GithubException e) {
        return handleException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    private Mono<ResponseEntity<?>> handleException(HttpStatus httpStatus, String message) {
        var status = httpStatus.value();

        var errorPropertiesMap = Map.of(
                "status", status,
                "Message", message
        );

        return Mono.just(ResponseEntity.status(status)
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .body(errorPropertiesMap));
    }
}
