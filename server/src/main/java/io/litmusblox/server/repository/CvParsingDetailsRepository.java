/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CvParsingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : Sumit
 * Date : 30/08/19
 * Time : 1:34 PM
 * Class Name : CvParsingDetailsRepository
 * Project Name : server
 */
public interface CvParsingDetailsRepository extends JpaRepository<CvParsingDetails, Long> {

    List<CvParsingDetails> findByRchilliJsonProcessed(boolean rchilliJsonProcessed);
}
