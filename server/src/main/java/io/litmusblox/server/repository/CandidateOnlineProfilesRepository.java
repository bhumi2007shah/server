/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Candidate;
import io.litmusblox.server.model.CandidateOnlineProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Sumit
 * Date : 25/07/19
 * Time : 6:28 PM
 * Class Name : CandidateOnlineProfilesRepository
 * Project Name : server
 */
public interface CandidateOnlineProfilesRepository extends JpaRepository<CandidateOnlineProfile, Long> {

    @Transactional
    //@Query(value = "DELETE FROM CANDIDATE_ONLINE_PROFILE cop WHERE cop.CANDIDATE_ID =: candidateId", nativeQuery = true)
    void deleteByCandidateId(/*@Param("candidateId")*/ Long candidateId);
}
