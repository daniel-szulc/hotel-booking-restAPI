package com.danielszulc.roomreserve.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ AuthenticationException.class })
    @ResponseBody
    public ResponseEntity<RestError> handleAuthenticationException(Exception ex) {

        RestError re = new RestError();
        re.setTimestamp(LocalDateTime.now());
        re.setMessage("Authentication failed at controller advice");
        re.setDetails(HttpStatus.UNAUTHORIZED.toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }

    @ExceptionHandler({ InvalidLoginException.class })
    @ResponseBody
    public ResponseEntity<RestError> handleInvalidLoginException(InvalidLoginException ex) {
        RestError re = new RestError();
        re.setTimestamp(LocalDateTime.now());
        re.setMessage(ex.getMessage());
        re.setDetails(HttpStatus.UNAUTHORIZED.toString());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestError> myAnyExceptionHandler(Exception ex, WebRequest req){
        RestError re = new RestError();
        re.setTimestamp(LocalDateTime.now());
        re.setMessage(ex.getMessage());
        re.setDetails(req.getDescription(false));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(re);
    }
}
