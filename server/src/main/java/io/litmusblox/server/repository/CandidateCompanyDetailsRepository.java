/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CandidateCompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;
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
    void deleteByCandidateId(Long candidateId);
}
