/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.CompanyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : Sonal Dedhia
 * Date : 9/9/19
 * Time : 1:34 PM
 * Class Name : CompanyHistoryRepository
 * Project Name : server
 */
@Repository
public interface CompanyHistoryRepository extends JpaRepository<CompanyHistory, Long> {
}
