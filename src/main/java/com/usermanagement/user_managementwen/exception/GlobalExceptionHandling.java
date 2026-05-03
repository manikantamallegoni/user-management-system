package com.usermanagement.user_managementwen.exception;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandling  extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ResourceNotFoundEception.class)
    public ResponseEntity<ErrorDeatails> resourceNotFoundExcepion(ResourceNotFoundEception exception, WebRequest webRequest){

        String errorCode;
        ErrorDeatails errorDeatails = new ErrorDeatails(
                LocalDateTime.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                "User_Not_Found"
        );


        return new ResponseEntity<>(errorDeatails, HttpStatus.NOT_FOUND);

    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {


        Map<String,String > errors=new HashMap<>();
       List<ObjectError> errorList= ex.getBindingResult().getAllErrors();
       errorList.forEach(error->{
           String fieldName=((FieldError)error).getField();
           String message=error.getDefaultMessage();
           errors.put(fieldName,message);
       });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
