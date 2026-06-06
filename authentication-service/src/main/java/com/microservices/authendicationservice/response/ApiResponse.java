package com.microservices.authendicationservice.response;

import java.util.Map;

public class ApiResponse<T>{



    private boolean success;
    private String message;
    private T data;
    private Map<String, String> errors;

    public ApiResponse(boolean success, String message, T data, Map<String, String> errors) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    public ApiResponse(boolean success, String message, T data) {
        this(success, message, data, null);
    }


    public ApiResponse(boolean success, String message, Map<String, String> errors) {
        this(success, message, null, errors);
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public Map<String, String> getErrors() { return errors; }









}