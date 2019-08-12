/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.error.WebException;
import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.*;
import io.litmusblox.server.security.JwtTokenUtil;
import io.litmusblox.server.service.LoginResponseBean;
import io.litmusblox.server.service.UserWorkspaceBean;
import io.litmusblox.server.utils.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @Resource
    JobCandidateMappingRepository jobCandidateMappingRepository;

    @Resource
    CompanyRepository companyRepository;

    @Resource
    CountryRepository countryRepository;

    @Resource
    JobRepository jobRepository;

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
        final User userDetails = (User)loadUserByUsername(user.getEmail());

        //check if company is active
        if(!userDetails.getCompany().getActive())
            throw new WebException("Company blocked", HttpStatus.FORBIDDEN);

        authenticate(user.getEmail(), user.getPassword());

        final String token = jwtTokenUtil.generateToken(userDetails, userDetails.getId(), userDetails.getCompany().getId());

        log.info("Completed processing login request in " + (System.currentTimeMillis() - startTime) +"ms.");

        return new LoginResponseBean(token, userDetails.getDisplayName(), userDetails.getCompany().getCompanyName(),jobCandidateMappingRepository.getUploadedCandidateCount(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), userDetails));
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(userName);
        if(null == user)
            throw new UsernameNotFoundException("User not found with email: " + userName);

        return user;
    }

    private void authenticate(String username, String password) throws Exception {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    /**
     * Service method to create a new user
     *
     * @param user the user to be created
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public User createUser(User user) throws Exception {

        User loggedInUser = getLoggedInUser();

        //check if the user is duplicate
        checkForDuplicateUser(user, loggedInUser.getRole());
        validateUser(user);

        Company companyObjToUse = null;
        if(IConstant.UserRole.Names.SUPER_ADMIN.equals(loggedInUser.getRole())) {
            //create a company
            companyObjToUse = companyRepository.save(new Company(user.getCompany().getCompanyName(), true, new Date(), loggedInUser.getId()));
        }

        User u = new User();
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        u.setEmail(user.getEmail());
        u.setCompany((companyObjToUse==null)?loggedInUser.getCompany():companyObjToUse);
        //If a superadmin is creating a user, the role should be set to client admin, else it should be Recruiter
        u.setRole(IConstant.UserRole.Names.SUPER_ADMIN.equals(loggedInUser.getRole())?IConstant.UserRole.Names.CLIENT_ADMIN:IConstant.UserRole.Names.RECRUITER);
        u.setCountryId(countryRepository.findByCountryCode(user.getCountryCode()));
        u.setMobile(user.getMobile());
        u.setUserUuid(UUID.randomUUID());
        u.setCreatedBy(loggedInUser.getId());
        u.setCreatedOn(new Date());

        return userRepository.save(u);
    }

    private User getLoggedInUser() {
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void checkForDuplicateUser(User user, String role) throws ValidationException {
        //check if user with email exists
        User dupUser = userRepository.findByEmail(user.getEmail());
        if (null != dupUser) {
            log.error("Duplicate user found: " + dupUser.toString());
            throw new ValidationException(IErrorMessages.DUPLICATE_USER_EMAIL + " - " + user.getEmail(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if(IConstant.UserRole.Names.SUPER_ADMIN.equals(role)) {
            //superadmin can only create the first user for any company
            //check that a user for the same company does not exist
            Company userCompany = companyRepository.findByCompanyName(user.getCompany().getCompanyName());
            if (null != userCompany) {
                List<User> usersForCompany = userRepository.findByCompanyId(userCompany.getId());
                if (null != usersForCompany && usersForCompany.size() > 0) {
                    //users for the company already exist, cannot create another
                    throw new ValidationException(IErrorMessages.CLIENT_ADMIN_EXISTS_FOR_COMPANY + user.getCompany().getCompanyName(), HttpStatus.EXPECTATION_FAILED);
                }
            }
        }
    }

    private void validateUser(User user) throws ValidationException {
        if (null == user.getCountryCode())
            throw new ValidationException(IErrorMessages.USER_COUNTRY_NULL, HttpStatus.UNPROCESSABLE_ENTITY);
        //validate firstName
        Util.validateName(user.getFirstName());
        //validate lastName
        Util.validateName(user.getLastName());
        //validate email
        Util.validateEmail(user.getEmail());
        //validate mobile
        Util.validateMobile(user.getMobile(), user.getCountryCode());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setPassword(User user) throws Exception {

        if (null == user.getCurrentPassword() || null == user.getConfirmPassword())
            throw new ValidationException(IErrorMessages.PASSWORD_MISMATCH, HttpStatus.UNPROCESSABLE_ENTITY);

        //verify that password and confirm password are same
        if(!user.getCurrentPassword().equals(user.getConfirmPassword()))
            throw new ValidationException(IErrorMessages.PASSWORD_MISMATCH, HttpStatus.UNPROCESSABLE_ENTITY);

        //verify that user exists
        User userToUpdate = userRepository.findByUserUuid(user.getUserUuid());
        if (null == userToUpdate)
            throw new ValidationException(IErrorMessages.USER_NOT_FOUND, HttpStatus.UNPROCESSABLE_ENTITY);
        User userByEmail = userRepository.findByEmail(user.getEmail());
        if (userToUpdate.getId() != userByEmail.getId())
            throw new ValidationException(IErrorMessages.USER_EMAIL_TOKEN_MISMATCH, HttpStatus.UNPROCESSABLE_ENTITY);

        userToUpdate.setPassword(passwordEncoder.encode(user.getCurrentPassword()));
        userToUpdate.setUserUuid(null);
        userToUpdate.setStatus(IConstant.UserStatus.Active.name());
        userToUpdate.setUpdatedOn(new Date());
        userToUpdate.setResetPasswordFlag(false);
        userRepository.save(userToUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void forgotPassword(String email) throws Exception {
        if(Util.isNull(email))
            throw new ValidationException(IErrorMessages.NO_EMAIL_PROVIDED, HttpStatus.BAD_REQUEST);

        User userToReset = userRepository.findByEmail(email);
        if (null == userToReset) {
            throw new ValidationException(IErrorMessages.USER_NOT_FOUND + email, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (IConstant.UserStatus.Blocked.name().equals(userToReset.getStatus())) {
            throw new ValidationException(IErrorMessages.FORGOT_PASSWORD_USER_BLOCKED+email, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        else if(IConstant.UserStatus.Active.name().equals(userToReset.getStatus())) {
            throw new ValidationException(IErrorMessages.USER_NOT_ACTIVE + email, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        else if(!IConstant.UserStatus.Active.name().equals(userToReset.getStatus())){
            throw new ValidationException(IErrorMessages.FORGOT_PASSWORD_DUPLICATE_REQUEST+email, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        userToReset.setPassword(null);
        userToReset.setUserUuid(UUID.randomUUID());
        userToReset.setStatus(IConstant.UserStatus.Inactive.name());
        userToReset.setResetPasswordFlag(true);
        userRepository.save(userToReset);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void blockUser(User user, boolean blockUser) {
        User objFromDb = userRepository.getOne(user.getId());
        if (null == objFromDb)
            throw new ValidationException("Invalid user", HttpStatus.UNPROCESSABLE_ENTITY);

        //if user is client admin, block the company
        if(objFromDb.getRole().equals(IConstant.UserRole.Names.CLIENT_ADMIN)) {
            if(blockUser) {
                Company companyToBlock = objFromDb.getCompany();
                companyToBlock.setActive(false);
                companyToBlock.setUpdatedBy(getLoggedInUser().getId());
                companyToBlock.setUpdatedOn(new Date());
                companyRepository.save(companyToBlock);
                log.info("Blocked company " + companyToBlock.getCompanyName());
            }
            else {
                if(!objFromDb.getCompany().getActive())
                    throw new ValidationException("Cannot unblock user of a blocked company");
            }
        }
        else {
            if (blockUser)
                objFromDb.setStatus(IConstant.UserStatus.Blocked.name());
            else {
                if (null == objFromDb.getPassword())
                    objFromDb.setStatus(IConstant.UserStatus.Inactive.name());
                else
                    objFromDb.setStatus(IConstant.UserStatus.Active.name());
            }
            objFromDb.setUpdatedBy(getLoggedInUser().getId());
            objFromDb.setUpdatedOn(new Date());

            userRepository.save(objFromDb);
        }
    }

    /**
     * Service method to fetch a list of all users for a company
     * @param companyName the company for which users need to be fetched
     * @return list of all users for the company
     * @throws Exception
     */
    public List<UserWorkspaceBean> fetchUsers(String companyName) throws Exception {
        log.info("Received request to get list of users");
        long startTime = System.currentTimeMillis();

        Company company = companyRepository.findByCompanyName(companyName);
        if(null == company)
            throw new ValidationException("Company not found: " + companyName, HttpStatus.UNPROCESSABLE_ENTITY);

        List<User> userList = userRepository.findByCompanyId(company.getId());
        List<UserWorkspaceBean> responseBeans = new ArrayList<>(userList.size());
        userList.forEach(user->{
            UserWorkspaceBean workspaceBean = new UserWorkspaceBean(user.getId(), user.getDisplayName(), user.getStatus());
            workspaceBean.setNumberOfJobsCreated(jobRepository.countByCreatedBy(user));
            responseBeans.add(workspaceBean);
        });

        log.info("Completed processing list of users in " + (System.currentTimeMillis() - startTime) + "ms.");
        return responseBeans;
    }
}