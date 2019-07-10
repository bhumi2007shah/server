/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.JobKeySkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : Sumit
 * Date : 8/7/19
 * Time : 12:12 PM
 * Class Name : JobKeySkillsRepository
 * Project Name : server
 */
public interface JobKeySkillsRepository extends JpaRepository<JobKeySkills, Long> {

    List<JobKeySkills> findByJobIdAndMlProvided(Long jobId, Boolean mlProvided);

    @Modifying
    @Query(value = "UPDATE JobKeySkills jks set ML_PROVIDED =:newBoolValue where jks.ML_PROVIDED =:oldBoolValue and jks.JOB_ID =:jobId", nativeQuery=true)
    void updateJobKeySkills(@Param("newBoolValue") Boolean newBoolValue, @Param("oldBoolValue") Boolean oldBoolValue, @Param("jobId") Long jobId);
}
