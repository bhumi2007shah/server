/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Bean to hold details for JobWorkSpace
 * This bean will be populated by the Jobservice layer
 * to provide only as much data as is required for Frontend to render the ag-grid
 *
 * @author : Shital Raval
 * Date : 9/7/19
 * Time : 11:32 AM
 * Class Name : JobWorspaceResponseBean
 * Project Name : server
 */
@Data
@NoArgsConstructor
public class JobWorspaceBean {
    private Long jobId;
    private String status;
    private String jobTitle;
    private String companyJobId;
    private String jobLocation;
    private int noOfPositions;
    private String businessUnit;
    private String function;
    private Date jobPosted;
    private String recruiter;

    public JobWorspaceBean(Long jobId, String status, String jobTitle, String companyJobId, int noOfPositions, Date jobPosted, String recruiter) {
        this.jobId = jobId;
        this.status = status;
        this.jobTitle = jobTitle;
        this.companyJobId = companyJobId;
        this.noOfPositions = noOfPositions;
        this.jobPosted = jobPosted;
        this.recruiter = recruiter;
    }
}
