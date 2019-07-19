/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.CompanyScreeningQuestionsRepository;
import io.litmusblox.server.repository.ScreeningQuestionsRepository;
import io.litmusblox.server.repository.UserRepository;
import io.litmusblox.server.repository.UserScreeningQuestionRepository;
import io.litmusblox.server.service.IScreeningQuestionService;
import io.litmusblox.server.service.ScreeningQuestionResponseBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Implementation class for ScreeningQuestion Service
 *
 * @author : Shital Raval
 * Date : 12/7/19
 * Time : 4:04 PM
 * Class Name : ScreeningQuestionService
 * Project Name : server
 */
@Service
public class ScreeningQuestionService implements IScreeningQuestionService {

    @Resource
    ScreeningQuestionsRepository screeningQuestionsRepository;

    @Resource
    UserScreeningQuestionRepository userScreeningQuestionRepository;

    @Resource
    CompanyScreeningQuestionsRepository companyScreeningQuestionsRepository;

    @Resource
    UserRepository userRepository;

    /**
     * Method to fetch custom screening questions for the company and user
     *
     * @throws Exception
     */
    @Override
    public ScreeningQuestionResponseBean fetchScreeningQuestionsForCompanyAndUser() throws Exception {

        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ScreeningQuestionResponseBean responseBean = new ScreeningQuestionResponseBean();
        responseBean.setCompanyScreeningQuestion(companyScreeningQuestionsRepository.findByCompanyId(loggedInUser.getCompanyId()));
        responseBean.setUserScreeningQuestion(userScreeningQuestionRepository.findByUserId(loggedInUser));

        return responseBean;
    }
}
