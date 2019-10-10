/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.JobCapabilityStarRatingMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : Shital Raval
 * Date : 10/10/19
 * Time : 12:04 PM
 * Class Name : JobCapabilityStarRatingMappingRepository
 * Project Name : server
 */
@Repository
public interface JobCapabilityStarRatingMappingRepository extends JpaRepository<JobCapabilityStarRatingMapping, Long> {
}
