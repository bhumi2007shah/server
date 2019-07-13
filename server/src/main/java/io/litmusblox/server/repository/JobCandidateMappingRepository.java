/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobCandidateMapping;
import io.litmusblox.server.model.MasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository class for JobCandidateMapping
 *
 * @author : Shital Raval
 * Date : 10/7/19
 * Time : 5:23 PM
 * Class Name : JobCandidateMappingRepository
 * Project Name : server
 */
public interface JobCandidateMappingRepository extends JpaRepository<JobCandidateMapping, Long> {

    //find by job and stage id
    @Transactional
    List<JobCandidateMapping> findByJobIdAndStage(Job jobId, MasterData stage) throws Exception;

    //find count of candidates per stage
    @Transactional
    @Query(value = "select stage, count(candidate_id) from job_candidate_mapping where job_id=:jobId group by stage", nativeQuery = true)
    List<Object[]> findCandidateCountByStage(Long jobId) throws Exception;
}
