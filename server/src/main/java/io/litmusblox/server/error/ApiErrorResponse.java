/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/**
 * Rest api response pojo
 *
 * @author : Shital Raval
 * Date : 6/8/19
 * Time : 12:00 PM
 * Class Name : ApiErrorResponse
 * Project Name : server
 */
@Data
@AllArgsConstructor
@ToString
public class ApiErrorResponse {
    private int errorCode;
    private String message;
}

