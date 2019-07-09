/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import io.litmusblox.server.model.Job;

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
    JobResponseBean addJob(Job job, String pageName) throws Exception;

    /**
     * Find all jobs for logged in user
     *
     * @param archived flag indicating if only archived jobs need to be fetched
     * @return response bean with list of jobs created by the user, count of active jobs and count of archived jobs
     * @throws Exception
     */
    JobWorspaceResponseBean findAllJobsForUser(boolean archived) throws Exception;
}
