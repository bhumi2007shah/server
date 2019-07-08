package io.litmusblox.server.service;

import io.litmusblox.server.model.User;

import java.util.Optional;

/**
 * Interface definition for User detail Service
 *
 * @author : Sameer
 * Date : 4/7/19
 * Time : 12:54 PM
 * Class Name : IUserService
 * Project Name : server
 */

public interface IUserService {
    User getUserById(Long id) throws Exception;
    User getUserByEmail(String email)throws Exception;
    String login(User requestUser)throws Exception;
}
