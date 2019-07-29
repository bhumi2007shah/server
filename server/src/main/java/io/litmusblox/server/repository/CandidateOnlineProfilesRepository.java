/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CandidateOnlineProfile;
import org.springframework.data.jpa.repository.JpaRepository;
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
    void deleteByCandidateId(Long candidateId);
}
