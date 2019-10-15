/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.CompanyBu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : Sumit
 * Date : 13/7/19
 * Time : 1:47 PM
 * Class Name : CompanyBuRepository
 * Project Name : server
 */
public interface CompanyBuRepository extends JpaRepository<CompanyBu, Long> {

    //find all BUs of company
    @Transactional
    List<CompanyBu> findByCompanyId(Long companyId);

    //find one business unit by business unit and companyId
    @Transactional
    CompanyBu findByBusinessUnitIgnoreCaseAndCompanyId(String businessUnit, Long companyId);
}
