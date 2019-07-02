/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.model.Job;
import io.litmusblox.server.repository.JobRepository;
import io.litmusblox.server.service.IJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation class for JobService
 *
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 9:47 AM
 * Class Name : JobService
 * Project Name : server
 */
@Service
public class JobService implements IJobService {

    @Autowired
    JobRepository jobRepository;

    @Override
    public List<Job> findAll() throws Exception {
        return jobRepository.findAll();
    }
}
