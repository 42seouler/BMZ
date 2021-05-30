package com.hotechcourse.oauth.resterror;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RestError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;
    private String debugMessage;
    private List<ApiSubError> subErrors;

    private RestError() {
        timestamp = LocalDateTime.now();
    }

    RestError(HttpStatus status) {
        this();
        this.status = status;
    }

    RestError(HttpStatus status, Throwable exception) {
        this();
        this.status = status;
        this.message = "Unexpected error";
        this.debugMessage = exception.getLocalizedMessage();
    }

    @Builder
    RestError(HttpStatus status, String message, Throwable exception) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = exception.getLocalizedMessage();
    }
}
