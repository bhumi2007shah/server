/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.WeightageCutoffMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Sumit
 * Date : 01/10/19
 * Time : 7:56 PM
 * Class Name : WeightageCutoffMappingRepository
 * Project Name : server
 */
public interface WeightageCutoffMappingRepository extends JpaRepository<WeightageCutoffMapping,Long> {

    @Transactional
    WeightageCutoffMapping findByWeightage(int weightage);

}
