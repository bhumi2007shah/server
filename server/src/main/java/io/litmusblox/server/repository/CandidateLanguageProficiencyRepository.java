/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CandidateLanguageProficiency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Sumit
 * Date : 25/07/19
 * Time : 8:03 PM
 * Class Name : CandidateLanguageProficiencyRepository
 * Project Name : server
 */
public interface CandidateLanguageProficiencyRepository extends JpaRepository<CandidateLanguageProficiency, Long> {

    @Transactional
    void deleteByCandidateId(Long candidateId);
}
