/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobHistory;
import io.litmusblox.server.model.JobStageStep;

import java.util.List;

/**
 * Interface definition for Job Service
 *
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 9:45 AM
 * Class Name : IJobService
 * Project Name : server
 */
public interface IJobService {
    /**
     * Add a new job
     * @return Response bean with jobId, and optionally list of skills and capabilities from ML
     * @throws Exception
     */
    Job addJob(Job job, String pageName) throws Exception;

    /**
     * Find all jobs for logged in user
     *
     * @param archived flag indicating if only archived jobs need to be fetched
     * @param companyName name of the company for which jobs have to be found
     * @return response bean with list of jobs created by the user, count of active jobs and count of archived jobs
     * @throws Exception
     */
    JobWorspaceResponseBean findAllJobsForUser(boolean archived, String companyName) throws Exception;

    /**
     * For the specified job, retrieve
     * 1. list candidates for job for specified stage
     * 2. count of candidates by each stage
     *
     * @param jobId the job id for which data is to be retrieved
     * @param stage the stage for which data is to be retrieved
     *
     * @return response bean with all details
     * @throws Exception
     */
    SingleJobViewResponseBean getJobViewById(Long jobId, String stage) throws Exception;

    /**
     * Service method to publish a job
     *
     * @param jobId id of the job to be published
     */
    void publishJob(Long jobId) throws Exception;

    /**
     * Service method to archive a job
     *
     * @param jobId id of the job to be archived
     */
    void archiveJob(Long jobId);

    /**
     * Service method to unarchive a job
     *
     * @param jobId id of the job to be unarchived
     */
    void unarchiveJob(Long jobId) throws Exception;

    /**
     * Service method to get job details by job id
     *
     * @param jobId id for which details will be retrieved
     */
    Job getJobDetails(Long jobId) throws Exception;

    /**
     * Service method to get job history by job id
     *
     * @param jobId id for which history will be retrieved
     *
     * @return a list of job history objects
     */
    List<JobHistory> getJobHistory(Long jobId) throws Exception;

    /**
     * Service method to return the stage steps for a job
     *
     * @param jobId the job id for which stage steps are to be returned
     * @return list of stage steps
     * @throws Exception
     */
    List<JobStageStep> getJobStageStep(Long jobId) throws Exception;
}
