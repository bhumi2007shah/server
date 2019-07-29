/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;


import io.litmusblox.server.model.CandidateEducationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Sumit
 * Date : 25/07/19
 * Time : 5:52 PM
 * Class Name : CandidateEducationDetailsRepository
 * Project Name : server
 */
public interface CandidateEducationDetailsRepository extends JpaRepository<CandidateEducationDetails, Long> {

    @Transactional
    void deleteByCandidateId(Long candidateId);
}
