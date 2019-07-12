/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobKeySkills;
import io.litmusblox.server.model.SkillsMaster;
import io.litmusblox.server.model.TempSkills;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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

    JobKeySkills findByJobIdAndSkillId(Long jobId, SkillsMaster skillsMaster);

   // JobKeySkills findByJobIdAndSkillIdFromTemp(Long jobId, TempSkills tempSkills);

    @Transactional
    @Modifying
    @Query(value = "UPDATE JOB_KEY_SKILLS jks set SELECTED =:newBoolValue where jks.ML_PROVIDED =:oldBoolValue and jks.JOB_ID =:jobId", nativeQuery=true)
    void updateJobKeySkills(@Param("newBoolValue") Boolean newBoolValue, @Param("oldBoolValue") Boolean oldBoolValue, @Param("jobId") Long jobId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE JOB_KEY_SKILLS jks set SELECTED =:selectedValue where jks.ID=:id", nativeQuery=true)
    void updateJobKeySkillById(@Param("selectedValue") Boolean selectedValue, @Param("id") Long id);
}
