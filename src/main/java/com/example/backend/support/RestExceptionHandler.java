package com.example.backend.support;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.io.IOException;
import java.sql.DataTruncation;
import java.sql.SQLException;
import java.util.Map;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request){
        String message = ex.getMessage();
        Map<String,String> body = Map.of("error",message == null ? "Entity not found" : message);
        return handleExceptionInternal(ex,body,new HttpHeaders(), HttpStatus.NOT_FOUND,request);
    }
    @ExceptionHandler(value = ExpiredJwtException.class)
    protected ResponseEntity<Object> handleExpiredToken(RuntimeException ex,WebRequest request){
        String message = ex.getMessage();
        Map<String,String> body = Map.of("error",message == null ? "Jwt token was expired" : message);
        return handleExceptionInternal(ex,body,new HttpHeaders(), HttpStatus.LOCKED,request);
    }
    @ExceptionHandler(value = {IllegalArgumentException.class, NullPointerException.class, MalformedJwtException.class,NumberFormatException.class})
    protected ResponseEntity<Object> handleInvalidArgument(RuntimeException ex,WebRequest request){
        String message = ex.getMessage();
        Map<String,String> body = Map.of("error",message == null ? "Invalid data received" : message);
        return handleExceptionInternal(ex,body,new HttpHeaders(), HttpStatus.BAD_REQUEST,request);
    }
    @ExceptionHandler(value = {EntityExistsException.class})
    protected ResponseEntity<Object> handleConflict(RuntimeException ex,WebRequest request){
        String message = ex.getMessage();
        Map<String,String> body = Map.of("error",message == null ? "Such value cannot be used, because other entity uses it" : message);
        return handleExceptionInternal(ex,body,new HttpHeaders(), HttpStatus.CONFLICT,request);
    }
    @ExceptionHandler(value = {IOException.class, DataTruncation.class})
    protected ResponseEntity<Object> handleInternalError(RuntimeException ex,WebRequest request){
        ex.printStackTrace();
        String message = ex.getMessage();
        Map<String,String> body = Map.of("error",message == null ? "Internal error" : message);
        return handleExceptionInternal(ex,body,new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,request);
    }
    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<Object> handleMaxSizeException(Exception e) {
        return new ResponseEntity<>("Uploaded file is too large, max size is 10 mb",HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
