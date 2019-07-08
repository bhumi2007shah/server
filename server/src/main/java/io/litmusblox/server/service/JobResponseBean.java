/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Sumit
 * Date : 5/7/19
 * Time : 1:03 PM
 * Class Name : JobResponseBean
 * Project Name : server
 */
@Data
public class JobResponseBean {

    private Long jobId;
    private Map<String, String> skillMap = new HashMap<>();
    private Map<String, String>  capabilityMap= new HashMap<>();
}
