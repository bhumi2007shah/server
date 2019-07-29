/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import io.litmusblox.server.model.Candidate;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * Response bean for upload candidates operation for a job
 *
 * @author : Shital Raval
 * Date : 16/7/19
 * Time : 5:08 PM
 * Class Name : UploadResponseBean
 * Project Name : server
 */
@Data
public class UploadResponseBean {

    private String status;
    private int successCount;
    private int failureCount;
    private List<Candidate> failedCandidates = new ArrayList();
    private int candidatesProcessedCount = 0;
}