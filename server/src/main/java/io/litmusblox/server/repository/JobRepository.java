/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository class for Job table related CRUD operations
 *
 * @author : shital
 * Date : 2/7/19
 * Time : 9:41 AM
 * Class Name : JobRepository
 * Project Name : server
 */

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    //find all jobs created by a user
    List<Job> findByCreatedBy(User createdBy);
    //find all jobs that are not archived
    List<Job> findByCreatedByAndDateArchivedIsNull(User createdBy);
    //find all archived jobs
    List<Job> findByCreatedByAndDateArchivedIsNotNull(User createdBy);

    //count of archived jobs
    Long countByCreatedByAndDateArchivedIsNotNull(User createdBy);

    //count of active jobs
    Long countByCreatedByAndDateArchivedIsNull(User createdBy);

}
