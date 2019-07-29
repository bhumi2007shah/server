/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.ConfigurationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for fetching Configuration settings at startup
 *
 * @author : Shital Raval
 * Date : 29/7/19
 * Time : 11:19 AM
 * Class Name : ConfigurationSettingsRepository
 * Project Name : server
 */
public interface ConfigurationSettingsRepository extends JpaRepository<ConfigurationSettings, Long> {
}
