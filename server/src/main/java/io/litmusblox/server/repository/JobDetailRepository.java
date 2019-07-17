/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobDetail;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for JobDetail table
 *
 * @author : Shital
 * Date : 17/7/19
 * Time : 12:51 PM
 * Class Name : JobDetailRepository
 * Project Name : server
 */
public interface JobDetailRepository extends JpaRepository<JobDetail, Long> {

    void deleteByJobId(Job jobId);
}
