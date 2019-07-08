/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.JobScreeningQuestions;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Sumit
 * Date : 5/7/19
 * Time : 5:17 PM
 * Class Name : JobScreeningQuestionsRepository
 * Project Name : server
 */
public interface JobScreeningQuestionsRepository extends JpaRepository<JobScreeningQuestions, Long> {
}
