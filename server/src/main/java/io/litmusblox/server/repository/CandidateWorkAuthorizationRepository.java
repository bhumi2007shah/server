/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Candidate;
import io.litmusblox.server.model.CandidateWorkAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Sumit
 * Date : 25/07/19
 * Time : 9:28 PM
 * Class Name : CandidateWorkAuthorizationRepository
 * Project Name : server
 */
public interface CandidateWorkAuthorizationRepository extends JpaRepository<CandidateWorkAuthorization, Long> {

    @Transactional
    //@Query(value = "DELETE FROM CANDIDATE_WORK_AUTHORIZATION cwa WHERE cwa.CANDIDATE_ID =: candidateId", nativeQuery = true)
    void deleteByCandidateId(/*@Param("candidateId")*/ Long candidateId);

}
