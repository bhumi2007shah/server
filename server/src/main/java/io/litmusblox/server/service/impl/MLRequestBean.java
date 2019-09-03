/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO for ML request
 *
 * @author : Shital Raval
 * Date : 27/8/19
 * Time : 11:10 AM
 * Class Name : MLRequestBean
 * Project Name : server
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MLRequestBean {
    private String jobTitle;
    private String jobDescription;
}
