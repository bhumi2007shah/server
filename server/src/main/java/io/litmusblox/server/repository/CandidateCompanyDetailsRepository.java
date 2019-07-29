/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CandidateCompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Sumit
 * Date : 26/07/19
 * Time : 9:42 AM
 * Class Name : CandidateCompanyDetailsRepository
 * Project Name : server
 */
public interface CandidateCompanyDetailsRepository extends JpaRepository<CandidateCompanyDetails, Long> {

    @Transactional
        /*@Query(value = "DELETE FROM CANDIDATE_COMPANY_DETAILS ccd WHERE ccd.CANDIDATE_ID =: candidateId", nativeQuery = true)*/
    void deleteByCandidateId(/*@Param("candidateId") */Long candidateId);
}
