/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.error.WebException;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.CompanyRepository;
import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.CompanyRepository;
import io.litmusblox.server.repository.UserRepository;
import io.litmusblox.server.service.CompanyWorspaceBean;
import io.litmusblox.server.service.ICompanyService;
import io.litmusblox.server.utils.StoreFileUtil;
import io.litmusblox.server.utils.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

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
@PropertySource("classpath:appConfig.properties")
@Log4j2
@Service
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

    @Resource
    CompanyRepository companyRepository;

    @Autowired
    Environment environment;

    //Update Company
    @Override
    public void saveCompany(Company company, MultipartFile logo) throws Exception {

        Company companyFromDb=companyRepository.findByCompanyName(company.getCompanyName());
        if(null==companyFromDb)
            throw new ValidationException("Company not found for this name "+company.getCompanyName());

        company.setId(companyFromDb.getId());

        if(null==company.getCompanyDescription() || company.getCompanyDescription().isEmpty()){
            throw new ValidationException("CompanyDescription " + IErrorMessages.EMPTY_AND_NULL_MESSAGE+ company.getId(), HttpStatus.BAD_REQUEST);
        }

        if(null==logo)
            throw new ValidationException("Company Logo " + IErrorMessages.NULL_MESSAGE+ company.getId(), HttpStatus.BAD_REQUEST);


        //Trim below fields if its length is greater than 245 and save trim string in db
        if (!Util.isNull(company.getWebsite()) && company.getWebsite().length() > 245){
            log.error("Company Website field exceeds limit -" +company.getWebsite());
            company.setWebsite(company.getWebsite().substring(0, 245));
        }

        if (!Util.isNull(company.getLinkedin()) && company.getLinkedin().length() > 245) {
            log.error("Company Linkedin field exceeds limit -" +company.getWebsite());
            company.setLinkedin(company.getLinkedin().substring(0, 245));
        }

        if (!Util.isNull(company.getTwitter()) && company.getTwitter().length() > 245) {
            log.error("Company Twitter field exceeds limit -" +company.getWebsite());
            company.setTwitter(company.getTwitter().substring(0, 245));
        }

        if (!Util.isNull(company.getFacebook()) && company.getFacebook().length() > 245) {
            log.error("Company Facebook field exceeds limit -" +company.getWebsite());
            company.setFacebook(company.getFacebook().substring(0, 245));
        }

        //Store Company logo on repo and save its filepath in to the company logo field
        String fileName = StoreFileUtil.storeFile(logo, company.getId(), environment.getProperty(IConstant.REPO_LOCATION), IConstant.UPLOAD_TYPE.Logo.toString(),null);
        log.info("Company " + company.getCompanyName() + " uploaded " + fileName);
        company.setLogo(fileName);


        if(null != companyFromDb) {
            company.setCreatedBy(companyFromDb.getCreatedBy());
            company.setCreatedOn(companyFromDb.getCreatedOn());
            company.setActive(companyFromDb.getActive());
            company.setSubscription(companyFromDb.getSubscription());
        }
        company.setUpdatedOn(new Date());
        company.setUpdatedBy(((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId());
        //Update Company
        companyRepository.save(company);
        log.info("Company Updated "+company.getId());
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
