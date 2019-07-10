/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.TempSkills;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Sumit
 * Date : 8/7/19
 * Time : 1:41 PM
 * Class Name : TempSkillsRepository
 * Project Name : server
 */
public interface TempSkillsRepository extends JpaRepository<TempSkills, Long> {
}
