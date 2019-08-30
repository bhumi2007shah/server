/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Response bean to be used to send back response with details about success / failure of different files uploaded as a part of single request
 *
 * @author : Shital Raval
 * Date : 21/8/19
 * Time : 10:42 AM
 * Class Name : CvUploadResponseBean
 * Project Name : server
 */
@Data
public class CvUploadResponseBean {
    //Map to be populated with key = "file name" and value = "success / failure with error message"
    private Map<String, String> cvUploadMessage = new HashMap<>();
    private String uploadRequestStatus;
}
