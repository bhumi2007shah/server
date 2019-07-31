/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.model.Company;
import io.litmusblox.server.service.ICompanyService;
import io.litmusblox.server.service.IScreeningQuestionService;
import io.litmusblox.server.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    ICompanyService companyService;

    @GetMapping("/screeningQuestions")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    String fetchScreeningQuestions() throws Exception {
        return Util.stripExtraInfoFromResponseBean(screeningQuestionService.fetchScreeningQuestionsForCompanyAndUser(),
            (new HashMap<String, List<String>>(){{
                put("UserClassFilter", Arrays.asList("displayName"));
            }}),
            new HashMap<String, List<String>>() {{
                put("CompanyScreeningQuestionFilter", Arrays.asList("createdOn", "createdBy", "updatedOn", "updatedBy","company"));
                put("UserScreeningQuestionFilter", Arrays.asList("createdOn", "updatedOn","userId"));
            }}
        );
    }

    /**
     * REST Api to update an existing company details
     * Only a client admin has access to this api
     *
     * @param company the company to be updated
     * @throws Exception
     */
    @PostMapping("/update")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('" + IConstant.UserRole.Names.CLIENT_ADMIN + "')")
    void updateCompany(@RequestParam("logo") MultipartFile logo,
                       @RequestParam("company") Company company) throws Exception {
        companyService.saveCompany(company, logo);
    }

}
