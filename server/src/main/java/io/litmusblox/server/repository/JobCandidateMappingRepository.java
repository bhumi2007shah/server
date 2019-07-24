/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
    List<JobCandidateMapping> findByJobAndStage(Job job, MasterData stage) throws Exception;

    //find count of candidates per stage
    @Transactional
    @Query(value = "select stage, count(candidate_id) from job_candidate_mapping where job_id=:jobId group by stage", nativeQuery = true)
    List<Object[]> findCandidateCountByStage(Long jobId) throws Exception;

    //find by job and Candidate
    @Transactional
    JobCandidateMapping findByJobAndCandidate(Job job, Candidate candidate);

    @Query(value = "select COUNT(jcm) from JOB_CANDIDATE_MAPPING jcm where jcm.CREATED_ON >=:createdOn and jcm.CREATED_BY =:user", nativeQuery = true)
    Integer getUploadedCandidateCount(@Param("createdOn") Date createdOn, @Param("user") User user);

}
