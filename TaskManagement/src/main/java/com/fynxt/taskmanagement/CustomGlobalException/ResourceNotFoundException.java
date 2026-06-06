package com.fynxt.taskmanagement.CustomGlobalException;

public class ResourceNotFoundException extends RuntimeException {
	
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
