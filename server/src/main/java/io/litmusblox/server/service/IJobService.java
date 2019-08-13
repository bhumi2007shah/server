/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobCandidateMapping;

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
     * @return response bean with list of jobs created by the user, count of active jobs and count of archived jobs
     * @throws Exception
     */
    JobWorspaceResponseBean findAllJobsForUser(boolean archived) throws Exception;

    /**
     * For the specified job, retrieve
     * 1. list candidates for job for specified stage
     * 2. count of candidates by each stage
     *
     * @param jobCandidateMapping The payload consisting of job id and stage
     *
     * @return response bean with all details
     * @throws Exception
     */
    SingleJobViewResponseBean getJobViewById(JobCandidateMapping jobCandidateMapping) throws Exception;

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
}
