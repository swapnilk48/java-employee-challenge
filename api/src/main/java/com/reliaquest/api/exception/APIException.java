package com.reliaquest.api.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Value
@Builder
public class APIException{
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    OffsetDateTime timestamp;
    int status;
    String error;
    String message;
    String path;

    public static APIException of(HttpStatus status, String message, String path) {
        return APIException.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
    }
}
