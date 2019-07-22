/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.UserRepository;
import io.litmusblox.server.security.JwtTokenUtil;
import io.litmusblox.server.service.LoginResponseBean;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Userdetails implementation class
 *
 * @author : Shital Raval
 * Date : 18/7/19
 * Time : 10:43 AM
 * Class Name : LbUserDetailsService
 * Project Name : server
 */
@Service
@Log4j2
public class LbUserDetailsService implements UserDetailsService {

    @Resource
    UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    /**
     * Implementation for login functionality which will
     * 1. authenticate the user
     * 2. generate and return the jwt token
     *
     * @param user the user to be logged in
     * @return responsebean with jwt token
     * @throws Exception
     */
    @Transactional
    public LoginResponseBean login(User user) throws Exception {
        log.info("Received login request from " + user.getEmail());
        long startTime = System.currentTimeMillis();
        authenticate(user.getEmail(), user.getPassword());

        final User userDetails = (User)loadUserByUsername(user.getEmail());
        final String token = jwtTokenUtil.generateToken(userDetails, userDetails.getId(), userDetails.getCompany().getId());

        log.info("Completed processing login request in " + (System.currentTimeMillis() - startTime) +"ms.");
        return new LoginResponseBean(token, userDetails.getDisplayName(), userDetails.getCompany().getCompanyName(),0);
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(userName);
        if(null == user)
            throw new UsernameNotFoundException("User not found with email: " + userName);

        return user;
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    //TODO: Add validations and checks
    public User createUser(User user) {

        User loggedInUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User u = new User();
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        u.setEmail(user.getEmail());
        u.setPassword(passwordEncoder.encode(user.getPassword()));
        u.setCompany(user.getCompany());
        u.setRole(user.getRole());
        u.setCountryId(user.getCountryId());
        u.setMobile(user.getMobile());
        u.setCreatedBy(loggedInUser.getId());
        u.setCreatedOn(new Date());

        return userRepository.save(u);
    }
}
