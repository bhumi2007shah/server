/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.JobCapabilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author : Sumit
 * Date : 8/7/19
 * Time : 5:50 PM
 * Class Name : JobCapabilitiesRepository
 * Project Name : server
 */
public interface JobCapabilitiesRepository extends JpaRepository<JobCapabilities, Long> {

    @Transactional
    void deleteByJobId(Long jobId);

    @Transactional
    List<JobCapabilities> findByJobId(Long jobId) throws Exception;

    @Transactional
    List<JobCapabilities> findByJobIdAndSelected(Long jobId, boolean b);
}
