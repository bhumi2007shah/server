/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CvParsingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Sumit
 * Date : 30/08/19
 * Time : 1:34 PM
 * Class Name : CvParsingDetailsRepository
 * Project Name : server
 */
//@Repository
public interface CvParsingDetailsRepository extends JpaRepository<CvParsingDetails, Long> {

}
