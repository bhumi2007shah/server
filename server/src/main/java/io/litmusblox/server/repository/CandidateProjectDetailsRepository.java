/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CandidateProjectDetails;
import org.springframework.data.jpa.repository.JpaRepository;
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
    void deleteByCandidateId(Long candidateId);
}
