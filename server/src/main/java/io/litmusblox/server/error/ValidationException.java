/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.error;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * @author : Sumit
 * Date : 19/7/19
 * Time : 12:28 PM
 * Class Name : ValidationException
 * Project Name : server
 */
@Data
public class ValidationException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 2980596172764187994L;
    private String errorMessage;
    private Integer errorCode;

    public ValidationException(String errorMessage)
    {
        super(errorMessage);
    }

    public ValidationException(String errorMessage, Integer errorCode)
    {
        super();
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public ValidationException(String errorMessage, HttpStatus badRequest) {
        super();
    }
}
