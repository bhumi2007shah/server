/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.JobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : Sonal Dedhia
 * Date : 9/9/19
 * Time : 1:34 PM
 * Class Name : JobHistoryRepository
 * Project Name : server
 */
@Repository
public interface JobHistoryRepository extends JpaRepository<JobHistory, Long> {
    List<JobHistory>findByJobIdOrderByIdDesc(Long jobId);
}
