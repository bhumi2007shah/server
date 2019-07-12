/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.JobCapabilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : Sumit
 * Date : 8/7/19
 * Time : 5:50 PM
 * Class Name : JobCapabilitiesRepository
 * Project Name : server
 */
public interface JobCapabilitiesRepository extends JpaRepository<JobCapabilities, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE JOB_CAPABILITIES jb set SELECTED =:boolValue where jb.JOB_ID =:jobId", nativeQuery=true)
    void updateJobCapabilitiesForUnSelected(@Param("boolValue") Boolean boolValue, @Param("jobId") Long jobId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE JOB_CAPABILITIES jb set SELECTED =:boolValue where jb.JOB_ID =:jobId and jb.ID IN :jobCapabilitiesIdList", nativeQuery=true)
    void updateJobCapabilitiesForSelected(@Param("boolValue") Boolean boolValue, @Param("jobId") Long jobId, @Param("jobCapabilitiesIdList")List<Long> jobCapabilitiesIdList);

    void deleteByJobId(Long jobId);

    List<JobCapabilities> findByJobId(Long jobId) throws Exception;

    @Query(value = "update job_capabilities set selected = false where job_id=:jobId", nativeQuery = true)
    void setSelectedFalseForJobId(Long jobId) throws Exception;
}
