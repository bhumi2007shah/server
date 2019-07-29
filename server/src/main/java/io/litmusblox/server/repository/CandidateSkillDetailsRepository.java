/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CandidateSkillDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Sumit
 * Date : 25/07/19
 * Time : 8:13 PM
 * Class Name : CandidateSkillDetailsRepository
 * Project Name : server
 */
public interface CandidateSkillDetailsRepository extends JpaRepository<CandidateSkillDetails, Long> {

    @Transactional
    //@Query(value = "DELETE FROM CANDIDATE_SKILL_DETAILS csd WHERE csd.CANDIDATE_ID =: candidateId", nativeQuery = true)
    void deleteByCandidateId(/*@Param("candidateId")*/ Long candidateId);
}
