/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author : Sumit
 * Date : 19/7/19
 * Time : 12:27 PM
 * Class Name : FieldErrorResource
 * Project Name : server
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldErrorResource {

    private String code;
    private String message;
}
