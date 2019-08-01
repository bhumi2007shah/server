/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.model.Company;
import io.litmusblox.server.repository.CompanyRepository;
import io.litmusblox.server.service.ICompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

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

    @Resource
    CompanyRepository companyRepository;

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

    /**
     * Service method to block or unblock a company
     * Only a super admin has access to this api
     *
     * @param company      the company to block
     * @param blockCompany flag indicating whether it is a block or an unblock operation
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void blockCompany(Company company, boolean blockCompany) throws Exception {
        Company companyObjFromDb = companyRepository.findByCompanyName(company.getCompanyName());
        if(null == companyObjFromDb)
            throw new ValidationException("Company not found: " + company.getCompanyName());
        companyObjFromDb.setActive(!blockCompany);
        companyRepository.save(companyObjFromDb);
    }
}
