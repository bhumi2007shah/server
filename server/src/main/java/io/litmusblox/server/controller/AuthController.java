/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import io.litmusblox.server.model.User;
import io.litmusblox.server.service.IUserService;
import io.litmusblox.server.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for all auth related functions like login, reset password, forgot password, acivate user
 *
 * @author : sameer
 * Date : 5/7/19
 * Time : 10:50 AM
 * Class Name : AuthController
 * Project Name : server
 */

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    IUserService userService;

    @PostMapping("/login")
    public String login(@RequestBody User user)throws Exception{
        String jwtToken = userService.login(user);
        return jwtToken;
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody User user){
        return "Test";
    }

    @PostMapping("/forgotPassword")
    public void forgotPassword(@RequestBody User user){

    }

    @PostMapping("/setNewPassword")
    public void setNewPassword(@RequestBody User user){

    }

}
