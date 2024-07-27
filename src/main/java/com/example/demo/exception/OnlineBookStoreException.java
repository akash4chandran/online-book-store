package com.example.demo.exception;

import java.io.Serial;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OnlineBookStoreException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private String message;
    private List<String> errorMessages;
    private HttpStatus status;

    public OnlineBookStoreException(String message, HttpStatus status) {
        super();
        this.message = message;
        this.status = status;
    }
}
