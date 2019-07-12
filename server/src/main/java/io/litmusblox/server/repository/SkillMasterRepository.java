/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.SkillsMaster;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author : Sumit
 * Date : 11/7/19
 * Time : 6:02 PM
 * Class Name : SkillMasterRepository
 * Project Name : server
 */
public interface SkillMasterRepository extends JpaRepository<SkillsMaster, Long> {

}
