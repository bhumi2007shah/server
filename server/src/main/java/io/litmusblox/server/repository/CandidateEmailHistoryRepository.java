/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CandidateEmailHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Sumit
 * Date : 17/7/19
 * Time : 12:06 PM
 * Class Name : CandidateEmailHistoryRepository
 * Project Name : server
 */
public interface CandidateEmailHistoryRepository extends JpaRepository<CandidateEmailHistory, Long> {

    CandidateEmailHistory findByEmail(String email);
}
