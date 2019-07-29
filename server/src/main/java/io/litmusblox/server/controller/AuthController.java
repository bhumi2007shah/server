/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.model.User;
import io.litmusblox.server.service.LoginResponseBean;
import io.litmusblox.server.service.impl.LbUserDetailsService;
import io.litmusblox.server.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Controller class for all authentication related apis like:
 * 1. Login
 * 2. Reset password
 * 3. Forgot password
 * 4. Activate user
 *
 * @author : Shital Raval
 * Date : 18/7/19
 * Time : 9:59 AM
 * Class Name : AuthController
 * Project Name : server
 */
@CrossOrigin(allowedHeaders = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    LbUserDetailsService userDetailsService;

    /**
     * Api to handle login request
     * @param user object with email and password
     * @return jwt token for authenticated user
     * @throws Exception
     */
    @PostMapping(value = "/login")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    LoginResponseBean login(@RequestBody User user) throws Exception {
        return userDetailsService.login(user);
    }

    @PostMapping(value = "/createUser")
    @PreAuthorize("hasRole('" + IConstant.UserRole.Names.SUPER_ADMIN + "') or hasRole('" + IConstant.UserRole.Names.CLIENT_ADMIN + "')")
    String addUser(@RequestBody User user) throws Exception {
        return Util.stripExtraInfoFromResponseBean(
                userDetailsService.createUser(user),
                (new HashMap<String, List<String>>(){{
                    put("UserClassFilter", Arrays.asList("id", "firstName", "lastName", "email","mobile"));
                }}),
                null
        );
    }
}
