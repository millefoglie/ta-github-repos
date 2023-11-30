package com.github.millefoglie.tagithubrepos.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        var defaultErrorAttributes = super.getErrorAttributes(request, options);
        var status = defaultErrorAttributes.getOrDefault("status", HttpStatus.INTERNAL_SERVER_ERROR);
        var message = defaultErrorAttributes.getOrDefault("error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        return Map.of(
                "status", status,
                "Message", message
        );
    }
}
