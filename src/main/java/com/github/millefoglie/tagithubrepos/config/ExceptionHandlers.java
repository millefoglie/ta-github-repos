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
        var status = HttpStatus.NOT_FOUND;
        var message = e.getMessage();

        var errorPropertiesMap = Map.of(
                "status", status,
                "Message", message
        );

        return Mono.just(ResponseEntity.status(status.value())
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .body(errorPropertiesMap));
    }

    @ExceptionHandler(GithubException.class)
    public Mono<ResponseEntity<?>> handleGithubException(GithubException e) {
        var status = HttpStatus.INTERNAL_SERVER_ERROR;
        var message = e.getMessage();

        var errorPropertiesMap = Map.of(
                "status", status,
                "Message", message
        );

        return Mono.just(ResponseEntity.status(status.value())
                                       .contentType(MediaType.APPLICATION_JSON)
                                       .body(errorPropertiesMap));
    }
}
