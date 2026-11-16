package com.clinch.crm.task.CustomGlobalException;

public class ResourceNotFoundException extends RuntimeException {
	
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
