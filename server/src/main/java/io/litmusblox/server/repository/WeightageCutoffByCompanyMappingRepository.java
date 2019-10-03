/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.WeightageCutoffByCompanyMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Sumit
 * Date : 01/10/19
 * Time : 7:40 PM
 * Class Name : WeightageCutoffByCompanyMappingRepository
 * Project Name : server
 */
public interface WeightageCutoffByCompanyMappingRepository extends JpaRepository<WeightageCutoffByCompanyMapping,Long> {

    @Transactional
    WeightageCutoffByCompanyMapping findByCompanyIdAndWeightage(long companyId, int weightage);
}
