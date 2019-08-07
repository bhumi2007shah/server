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
 * Time : 12:32 PM
 * Class Name : WebException
 * Project Name : server
 */
@Data
public class WebException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 2980596172764187994L;
    private String errorMessage;
    private HttpStatus errorCode;

    public WebException(String errorMessage, HttpStatus errorCode)
    {
        super();
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public WebException(String message, Throwable cause)
    {
        super(message, cause);

    }

    public WebException(String errorMessage, HttpStatus errorCode, Throwable cause)
    {
        super(cause);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
}
