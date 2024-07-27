package com.example.demo.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class OnlineBookStoreExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(OnlineBookStoreException.class)
    public ResponseEntity<ErrorResponse>
            handleOnlineBookStoreException(OnlineBookStoreException onlineBookStoreException) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(onlineBookStoreException.getStatus().value());
        errorResponse.setMessage(onlineBookStoreException.getStatus().name());
        String message = onlineBookStoreException.getMessage();
        List<String> errorMessages = onlineBookStoreException.getErrorMessages();
        if (Objects.isNull(message) && !errorMessages.isEmpty()) {
            errorResponse.setDetails(errorMessages);
        } else {
            errorResponse.setDetails(List.of(message));
        }

        return new ResponseEntity<>(errorResponse, onlineBookStoreException.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(status.value());
        errorResponse.setMessage(HttpStatus.valueOf(status.value()).name());
        errorResponse.setDetails(details);

        return new ResponseEntity<>(errorResponse, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(status.value());
        errorResponse.setMessage(HttpStatus.valueOf(status.value()).name());
        details.add(ex.getLocalizedMessage().split(":")[0]);
        errorResponse.setDetails(details);
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ErrorResponse> handlePropertyReferenceException(PropertyReferenceException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage("Invalid property reference");
        errorResponse.setDetails(List.of(ex.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> details = new ArrayList<>();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatusCode(status.value());
        errorResponse.setMessage(HttpStatus.valueOf(status.value()).name());
        details.add(ex.getMessage().split(":")[0]);
        errorResponse.setDetails(details);

        return new ResponseEntity<>(errorResponse, status);
    }

}