/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.error;

import lombok.Data;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 5:42 PM
 * Class Name : ErrorResponse
 * Project Name : server
 */
@Data
@XmlRootElement(name = "error")
public class ErrorResponse {

    private String message;
    private List<String> details;

    public ErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }
}
