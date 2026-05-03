package com.usermanagement.user_managementwen.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND)
public class ResourceNotFoundEception extends RuntimeException{
    private String resourceName;
    private String fieldName;
    private Object fieldValue;
    public ResourceNotFoundEception(String resourceName, String fieldName, Object fieldValue) {
       super(String.format("%s not found with %s : '%s'",resourceName,fieldName,fieldValue));
       this.resourceName = resourceName;
       this.fieldName = fieldName;
       this.fieldValue= fieldValue;
    }
}
