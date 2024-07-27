package com.example.demo.exception;

import java.util.List;

import lombok.Data;

@Data
public class ErrorResponse {
    private int statusCode;
    private String message;
    private List<String> details;
}