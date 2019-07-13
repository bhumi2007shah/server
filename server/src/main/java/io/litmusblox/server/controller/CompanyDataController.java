/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import io.litmusblox.server.Util;
import io.litmusblox.server.service.IScreeningQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * REST controller for company specific data operations
 *
 * @author : Shital Raval
 * Date : 12/7/19
 * Time : 6:32 PM
 * Class Name : CompanyDataController
 * Project Name : server
 */
@CrossOrigin
@RestController
@RequestMapping("/api/company")
public class CompanyDataController {

    @Autowired
    IScreeningQuestionService screeningQuestionService;

    @GetMapping("/screeningQuestions")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    String fetchScreeningQuestions() throws Exception {
        return Util.stripExtraInfoFromResponseBean(screeningQuestionService.fetchScreeningQuestionsForCompanyAndUser(),
            (new HashMap<String, List<String>>(){{
                put("UserClassFilter", Arrays.asList("displayName"));
            }}),
            new HashMap<String, List<String>>() {{
                put("CompanyScreeningQuestionFilter", Arrays.asList("createdOn", "createdBy", "updatedOn", "updatedBy","companyId"));
                put("UserScreeningQuestionFilter", Arrays.asList("createdOn", "updatedOn","userId"));
            }}
        );
    }
}
