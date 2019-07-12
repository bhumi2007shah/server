/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import io.litmusblox.server.model.CompanyScreeningQuestion;
import io.litmusblox.server.model.UserScreeningQuestion;
import lombok.Data;

import java.util.List;

/**
 * Bean to hold company and user specific screening questions
 *
 * @author : Shital Raval
 * Date : 12/7/19
 * Time : 6:36 PM
 * Class Name : ScreeningQuestionResponseBean
 * Project Name : server
 */
@Data
public class ScreeningQuestionResponseBean {
    private List<CompanyScreeningQuestion> companyScreeningQuestion;
    private List<UserScreeningQuestion> userScreeningQuestion;
}
