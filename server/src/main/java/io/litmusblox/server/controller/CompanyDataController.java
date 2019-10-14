/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.CompanyAddress;
import io.litmusblox.server.model.MasterData;
import io.litmusblox.server.model.User;
import io.litmusblox.server.service.ICompanyService;
import io.litmusblox.server.service.IScreeningQuestionService;
import io.litmusblox.server.utils.Util;
import lombok.extern.log4j.Log4j2;
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
@CrossOrigin(allowedHeaders = "*")
@RestController
@RequestMapping("/api/company")
@Log4j2
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
                put("User", Arrays.asList("displayName"));
            }}),
            new HashMap<String, List<String>>() {{
                put("CompanyScreeningQuestion", Arrays.asList("createdOn", "createdBy", "updatedOn", "updatedBy","company"));
                put("UserScreeningQuestion", Arrays.asList("createdOn", "updatedOn","userId"));
            }}
        );
    }

    /**
     * REST Api to update an existing company details
     * Only a client admin has access to this api
     *
     * @param companyString the company to be updated
     * @throws Exception
     */
    @PutMapping(value = "/update",consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('" + IConstant.UserRole.Names.CLIENT_ADMIN + "')")
    void updateCompany(@RequestParam("logo") MultipartFile logo,
                       @RequestParam("company") String companyString) throws Exception {
        Company company=new ObjectMapper().readValue(companyString, Company.class);
        companyService.saveCompany(company, logo);
    }


    /**
     * REST Api to block or unblock a company
     * Only a super admin has access to this api
     *
     * @param company the company to block
     * @param blockCompany flag indicating whether it is a block or an unblock operation
     * @throws Exception
     */
    @PutMapping(value = "/blockUnblockCompany")
    @PreAuthorize("hasRole('" + IConstant.UserRole.Names.SUPER_ADMIN + "')")
    @ResponseStatus(value = HttpStatus.OK)
    void blockCompany(@RequestBody Company company, @RequestParam boolean blockCompany) throws Exception {
        log.info("Received request to block company with name: "+ company.getCompanyName());
        long startTime = System.currentTimeMillis();
        companyService.blockCompany(company,blockCompany);
        log.info("Complete block company request in " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * REST Api to return a list of users for a given company
     * @param company the company name for which the list of users needs to be sent
     * @return List of users
     * @throws Exception
     */
    @GetMapping("/usersForCompany")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    List<User> findUserList(@RequestParam String company) throws Exception {
       //TODO: Make a call to the service layer and return appropriate list
       return null;
    }


    /**
     * REST Api to return a list of BUs for a given company
     * @param company the company name for which the list of BUs needs to be found
     * @return List of BUs
     * @throws Exception
     */
    @GetMapping("/buForCompany")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    List<User> findBuList(@RequestParam String company) throws Exception {
        //TODO: Make a call to the service layer and return appropriate list
        return null;
    }

    /**
     * REST Api to return a list of addresses for the company by address type
     * @param company the company name for which the list of addresses needs to be found
     * @param addressType the type of address, like job location, interview or both. The master data corresponding to one of these selections should be passed to the api call
     * @return List of company addresses
     * @throws Exception
     */
    @GetMapping("/addressByCompanyByType")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    List<CompanyAddress> findAddressByCompanyByType(@RequestParam String company, @RequestParam MasterData addressType) throws Exception {
        //TODO: Make a call to the service layer and return appropriate list
        return null;
    }

}
