/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.error;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 5:47 PM
 * Class Name : CustomExceptionHandler
 * Project Name : server
 */
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Server error", details);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<Object> handleValidationExceptions(ValidationException validationEx, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(validationEx.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Validation Failed", details);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<Object> handleDataIntegrityViolationExceptions(DataIntegrityViolationException dataIntegrityViolationEx, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(dataIntegrityViolationEx.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Wrong data inputs", details);
        return new ResponseEntity(error, HttpStatus.CONFLICT);
    }

}
