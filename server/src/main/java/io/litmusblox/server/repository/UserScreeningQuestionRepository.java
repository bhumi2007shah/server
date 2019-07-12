/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.UserScreeningQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for UserScreeningQuestion
 *
 * @author : Shital Raval
 * Date : 5/7/19
 * Time : 12:29 PM
 * Class Name : UserScreeningQuestionRepository
 * Project Name : server
 */
public interface UserScreeningQuestionRepository extends JpaRepository<UserScreeningQuestion,Long> {
}
