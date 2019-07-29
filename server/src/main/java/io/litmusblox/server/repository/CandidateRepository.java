/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Sumit
 * Date : 17/7/19
 * Time : 8:05 PM
 * Class Name : CandidateRepository
 * Project Name : server
 */
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
}
