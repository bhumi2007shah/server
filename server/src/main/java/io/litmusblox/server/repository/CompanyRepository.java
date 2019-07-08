/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for Company
 *
 * @author : Shital Raval
 * Date : 8/7/19
 * Time : 3:04 PM
 * Class Name : CompanyRepository
 * Project Name : server
 */
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
