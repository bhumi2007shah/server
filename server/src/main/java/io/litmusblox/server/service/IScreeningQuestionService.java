/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

/**
 * Service interface for handling screening questions for
 * 1. Master screening questions
 * 2. Company level screening questions
 * 3. User level screening questions
 *
 *
 * @author : Shital Raval
 * Date : 12/7/19
 * Time : 4:01 PM
 * Class Name : IScreeningQuestionService
 * Project Name : server
 */
public interface IScreeningQuestionService {

    /**
     * Method to fetch custom screening questions for the company and user
     *
     * @throws Exception
     */
    ScreeningQuestionResponseBean fetchScreeningQuestionsForCompanyAndUser() throws Exception;
}
