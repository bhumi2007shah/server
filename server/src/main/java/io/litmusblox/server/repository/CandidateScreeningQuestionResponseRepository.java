/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CandidateScreeningQuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for CandidateScreeningQuestionResponse
 *
 * @author : Shital Raval
 * Date : 23/7/19
 * Time : 11:20 AM
 * Class Name : CandidateScreeningQuestionResponseRepository
 * Project Name : server
 */
public interface CandidateScreeningQuestionResponseRepository extends JpaRepository<CandidateScreeningQuestionResponse,Long> {
}
