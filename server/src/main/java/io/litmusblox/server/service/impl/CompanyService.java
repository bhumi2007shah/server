/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.CompanyRepository;
import io.litmusblox.server.repository.UserRepository;
import io.litmusblox.server.service.CompanyWorspaceBean;
import io.litmusblox.server.service.ICompanyService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
@Log4j2
public class CompanyService implements ICompanyService {

    @Resource
    CompanyRepository companyRepository;

    @Resource
    UserRepository userRepository;

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
        companyObjFromDb.setUpdatedOn(new Date());
        companyObjFromDb.setUpdatedBy(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
        companyRepository.save(companyObjFromDb);
    }

    /**
     * Service method to fetch a list of all companies
     *
     * @return List of companies
     * @throws Exception
     */
    @Override
    public List<CompanyWorspaceBean> getCompanyList() throws Exception {
        log.info("Received request to get list of companies");
        long startTime = System.currentTimeMillis();

        List<Company> companies = companyRepository.findAll();

        List<CompanyWorspaceBean> responseBeans = new ArrayList<>(companies.size());

        companies.forEach(company -> {
            CompanyWorspaceBean worspaceBean = new CompanyWorspaceBean(company.getId(), company.getCompanyName(),
                    company.getCreatedOn(), !company.getActive());
            worspaceBean.setNumberOfUsers(userRepository.countByCompanyId(company.getId()));
            responseBeans.add(worspaceBean);
        });

        log.info("Completed processing list of companies in " + (System.currentTimeMillis() - startTime) + "ms.");
        return responseBeans;
    }
}
