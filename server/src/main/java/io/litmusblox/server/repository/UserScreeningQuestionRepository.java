/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.User;
import io.litmusblox.server.model.UserScreeningQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository class for UserScreeningQuestion
 *
 * @author : Shital Raval
 * Date : 12/7/19
 * Time : 4:05 PM
 * Class Name : UserScreeningQuestionRepository
 * Project Name : server
 */
public interface UserScreeningQuestionRepository extends JpaRepository<UserScreeningQuestion,Long> {
    @Transactional
    List<UserScreeningQuestion> findByUserId(User user);
}
