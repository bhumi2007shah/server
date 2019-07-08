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
     * List all Jobs
     * @return List of Jobs
     * @throws Exception
     */
    JobResponseBean addJob(Job job, String pageName);
}
