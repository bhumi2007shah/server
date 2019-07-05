/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 2:17 PM
 * Class Name : CountryRepository
 * Project Name : server
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
