/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CompanyStageStep;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Sumit
 * Date : 16/7/19
 * Time : 3:23 PM
 * Class Name : CompanyStageStepRepository
 * Project Name : server
 */
public interface CompanyStageStepRepository extends JpaRepository<CompanyStageStep, Long> {
}
