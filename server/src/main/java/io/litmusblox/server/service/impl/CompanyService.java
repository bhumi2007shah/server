/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.model.Company;
import io.litmusblox.server.service.ICompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveCompany(Company company) throws Exception {
       // TODO: add logic to create / update a company
    }
}
