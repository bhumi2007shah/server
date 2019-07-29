/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CandidateProjectDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Sumit
 * Date : 25/07/19
 * Time : 6:15 PM
 * Class Name : CandidateProjectDetailsRepository
 * Project Name : server
 */
public interface CandidateProjectDetailsRepository extends JpaRepository<CandidateProjectDetails, Long> {

    @Transactional
    //@Query(value = "DELETE FROM CANDIDATE_PROJECT_DETAILS cpd WHERE ced.CANDIDATE_ID =: candidateId", nativeQuery = true)
    void deleteByCandidateId(/*@Param("candidateId")*/ Long candidateId);
}
