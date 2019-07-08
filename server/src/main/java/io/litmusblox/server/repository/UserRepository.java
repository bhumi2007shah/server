/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.repository;

import io.litmusblox.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository class for User object
 *
 * @author : Shital Raval
 * Date : 8/7/19
 * Time : 3:03 PM
 * Class Name : UserRepository
 * Project Name : server
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
