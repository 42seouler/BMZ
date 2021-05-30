package com.hotechcourse.oauth.resterror;

import com.hotechcourse.oauth.exception.TokenRefreshException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(RestError restError) {
        return new ResponseEntity<>(restError, restError.getStatus());
    }

    @ExceptionHandler({TokenRefreshException.class})
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, final TokenRefreshException e) {
        return buildResponseEntity(RestError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("Requested with invalid RefreshToken.")
                .exception(e)
                .build());
    }
}
