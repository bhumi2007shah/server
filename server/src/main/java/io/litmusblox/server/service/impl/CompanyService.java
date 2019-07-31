/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.model.Company;
import io.litmusblox.server.service.ICompanyService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service class to perform various operations on a company
 *
 * @author : Shital Raval
 * Date : 30/7/19
 * Time : 2:12 PM
 * Class Name : CompanyService
 * Project Name : server
 */
@Service
public class CompanyService implements ICompanyService {
    /**
     * Service method to add / update a company
     *
     * @param company the company to be added
     * @param logo    file containing the company logo
     * @throws Exception
     */
    @Override
    public void saveCompany(Company company, MultipartFile logo) throws Exception {
        // TODO: add logic to update a company
        //retrieve company based on the id in the company object
        //save the logo to repolocation and the location of the file in the company logo column of db
    }
}
