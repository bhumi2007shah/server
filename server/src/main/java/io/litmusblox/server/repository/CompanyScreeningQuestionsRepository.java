/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CompanyScreeningQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository class for Company screening questions
 *
 * @author : Shital Raval
 * Date : 12/7/19
 * Time : 4:05 PM
 * Class Name : ScreeningQuestionsRepository
 * Project Name : server
 */
public interface CompanyScreeningQuestionsRepository extends JpaRepository<CompanyScreeningQuestion, Long> {
    @Transactional
    List<CompanyScreeningQuestion> findByCompanyId(Long companyId);
}
