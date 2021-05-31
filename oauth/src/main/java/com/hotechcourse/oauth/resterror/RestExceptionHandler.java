package com.hotechcourse.oauth.resterror;

import com.hotechcourse.oauth.exception.BadRequestException;
import com.hotechcourse.oauth.exception.ResourceNotFoundException;
import com.hotechcourse.oauth.exception.TokenRefreshException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@ControllerAdvice
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

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, final BadRequestException e) {
        return buildResponseEntity(RestError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message("다른 아이디를 입력하세요.")
                .exception(e)
                .build());
    }
}
