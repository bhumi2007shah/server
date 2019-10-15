/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import com.google.maps.model.LatLng;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.error.WebException;
import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.*;
import io.litmusblox.server.service.CompanyWorspaceBean;
import io.litmusblox.server.service.ICompanyService;
import io.litmusblox.server.service.MasterDataBean;
import io.litmusblox.server.utils.GoogleMapsCoordinates;
import io.litmusblox.server.utils.StoreFileUtil;
import io.litmusblox.server.utils.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class to perform various operations on a company
 *
 * @author : Shital Raval
 * Date : 30/7/19
 * Time : 2:12 PM
 * Class Name : CompanyService
 * Project Name : server
 */
@Log4j2
@Service
public class CompanyService implements ICompanyService {

    @Resource
    CompanyRepository companyRepository;

    @Resource
    UserRepository userRepository;

    @Autowired
    Environment environment;

    @Autowired
    CompanyHistoryRepository companyHistoryRepository;

    @Autowired
    CompanyBuRepository companyBuRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    CompanyAddressRepository companyAddressRepository;

    //Update Company
    @Override
    public Company saveCompany(Company company, MultipartFile logo) throws Exception {
        User loggedInUser  = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        log.info("Received request to update job from user: " + loggedInUser.getEmail());
        long startTime = System.currentTimeMillis();

        Company companyFromDb=companyRepository.findByCompanyNameIgnoreCase(company.getCompanyName());
        if(null==companyFromDb)
            throw new ValidationException("Company not found for this name "+company.getCompanyName(), HttpStatus.BAD_REQUEST);

        company.setId(companyFromDb.getId());

        if(company.getNewCompanyBu()!=null || company.getDeletedCompanyBu()!=null) {
            updateBusinessUnit(company, loggedInUser);
        }
        else if(company.getNewCompanyAddress()!=null || company.getDeletedCompanyAddress()!=null || company.getUpdatedCompanyAddress()!=null) {
            updateCompanyAddresses(company, companyFromDb, loggedInUser);
        }
        else {
            updateCompany(company, companyFromDb, loggedInUser, logo);
        }
            /*case UsersAndTeams:
                updateUsersAndTeams(company, companyFromDb, loggedInUser);
                break;
            case ScreeningQuestions:
                updateScreeningQuestions(company, companyFromDb, loggedInUser);
                break;*/

        log.info("Completed processing request to update company in " + (System.currentTimeMillis() - startTime) + "ms");
        return company;
    }

    /**
     * @param companyFromDb
     * @param company
     * @param loggedInUser
     * @param logo
     */
    private void updateCompany(Company company, Company companyFromDb, User loggedInUser, MultipartFile logo) {

         /*
         * 14-10-2019
         * Logo is optional so removed exception handling.
        if(null==logo)
            throw new ValidationException("Company Logo " + IErrorMessages.NULL_MESSAGE+ company.getId(), HttpStatus.BAD_REQUEST);
        */

        if(null==company.getCompanyDescription() || company.getCompanyDescription().isEmpty()){
            throw new ValidationException("CompanyDescription " + IErrorMessages.EMPTY_AND_NULL_MESSAGE+ company.getId(), HttpStatus.BAD_REQUEST);
        }
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

        //Store Company logo on repo and save its filepath in to the company logo field if logo in not null
        if(logo != null) {
            String fileName = null;
            try{
                fileName = StoreFileUtil.storeFile(logo, company.getId(), environment.getProperty(IConstant.REPO_LOCATION), IConstant.UPLOAD_TYPE.Logo.toString(), null, null);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            log.info("Company " + company.getCompanyName() + " uploaded " + fileName);
            company.setLogo(fileName);
        }


        if(null != companyFromDb) {
            company.setCreatedBy(companyFromDb.getCreatedBy());
            company.setCreatedOn(companyFromDb.getCreatedOn());
            company.setActive(companyFromDb.getActive());
            company.setSubscription(companyFromDb.getSubscription());
        }
        //Update Company
        companyRepository.save(company);
        saveCompanyHistory(company.getId(), "Update company information", loggedInUser);
        log.info("Company Updated "+company.getId());
    }

    /**
     *
     * @param company
     * @param loggedInUser
     */
    private void updateBusinessUnit(Company company, User loggedInUser) {
        Map<String, String> errorResponse= new HashMap<>();

        //process new company BU's
        if(company.getNewCompanyBu().size()>0) {
            company.getNewCompanyBu().stream().forEach(businessUnit -> {
                CompanyBu companyBuFromDb = companyBuRepository.findByBusinessUnitIgnoreCaseAndCompanyId(businessUnit, company.getId());
                if (null != companyBuFromDb) {
                    errorResponse.put(businessUnit, "Already exist");
                } else {
                    CompanyBu companyBu = new CompanyBu();
                    companyBu.setCompanyId(company.getId());
                    companyBu.setBusinessUnit(businessUnit);
                    companyBu.setCreatedBy(loggedInUser.getId());
                    companyBu.setCreatedOn(new Date());
                    companyBuRepository.save(companyBu);
                }
            });
        }

        //process deleted company BUs
        if(company.getDeletedCompanyBu().size()>0){
            company.getDeletedCompanyBu().stream().forEach(businessUnit -> {
                CompanyBu companyBuFromDb = companyBuRepository.findByBusinessUnitIgnoreCaseAndCompanyId(businessUnit, company.getId());
                if(null!=companyBuFromDb) {
                    int jobsCount = jobRepository.countByBuId(companyBuFromDb);
                    if (jobsCount == 0) {
                        companyBuRepository.delete(companyBuFromDb);
                    } else {
                        errorResponse.put(businessUnit, jobsCount + "jobs available for this BU");
                    }
                }
                else{
                    errorResponse.put(businessUnit, "does not exist");
                }
            });
        }

        companyBuRepository.flush();
        saveCompanyHistory(company.getId(), "Updated company BUs", loggedInUser);

        if(errorResponse.size()>0) {
            log.info("Updated Company BU's with errors: " + errorResponse);
            throw new WebException("Error while updating BU's: "+errorResponse, HttpStatus.UNPROCESSABLE_ENTITY, errorResponse);
        }
        log.info("Company BUs' Updated for company Id: "+company.getId());
        company.setCompanyBuList(companyBuRepository.findByCompanyId(company.getId()));
    }

    /**
     *
     * @param company
     * @param companyFromDb
     * @param loggedInUser
     */
    private void updateUsersAndTeams(Company company, Company companyFromDb, User loggedInUser) {
    }

    /**
     *
     * @param company
     * @param companyFromDb
     * @param loggedInUser
     */
    private void updateScreeningQuestions(Company company, Company companyFromDb, User loggedInUser) {
    }

    /**
     *
     * @param company
     * @param companyFromDb
     * @param loggedInUser
     */
    private void updateCompanyAddresses(Company company, Company companyFromDb, User loggedInUser) {
        Map<String, String> errorResponse = new HashMap<>();

        //process new company Addresses
        if(company.getNewCompanyAddress().size()>0) {
            company.getNewCompanyAddress().stream().forEach(address -> {
                LatLng coordinates = null;
                try {
                    coordinates = GoogleMapsCoordinates.getCoordinates(address.getAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //add error to errorResponse if no cordinates are found.
                if(null==coordinates){
                    errorResponse.put(address.getAddressTitle(), "coordinates not found");
                }
                else {
                    LatLng finalCoordinates = coordinates;
                    Boolean addressExists = companyFromDb.getCompanyAddressList()
                                            .stream().filter(companyAddress -> {
                                                return companyAddress.getLatitude()== finalCoordinates.lat && companyAddress.getLongitude()== finalCoordinates.lng;
                                            })
                                            .collect(Collectors.toList())
                                            .size() > 0;

                    if (addressExists) {
                        errorResponse.put(address.getAddressTitle(), "Already exist");
                    } else {
                        address.setCompanyId(company.getId());
                        address.setLatitude(coordinates.lat);
                        address.setLongitude(coordinates.lng);
                        address.setCreatedBy(loggedInUser.getId());
                        address.setCreatedOn(new Date());
                        companyAddressRepository.save(address);
                    }
                }
            });
        }

        //process deleted company Addresses
        if(company.getDeletedCompanyAddress().size()>0){
            company.getDeletedCompanyAddress().stream().forEach(companyAddress -> {
                log.info("deleting company address with id: "+companyAddress.getId());
                CompanyAddress companyAddressFromDb = companyAddressRepository.findById(companyAddress.getId()).orElse(null);
                if(companyAddressFromDb!=null) {
                    int jobsCount = jobRepository.countByJobLocationOrInterviewLocation(companyAddress, companyAddress);
                    if (jobsCount == 0) {
                        companyAddressRepository.delete(companyAddress);
                        log.info("deleted company address with id: "+companyAddress.getId());
                    } else {
                        errorResponse.put(companyAddress.getAddressTitle(), jobsCount + "jobs available for this BU");
                    }
                }
                else{
                    errorResponse.put(companyAddress.getAddressTitle(), "does not exist");
                }
            });
        }

        //process updated company Address
        if(company.getUpdatedCompanyAddress().size()>0){
            company.getUpdatedCompanyAddress().stream().forEach(companyAddress -> {
                log.info("updating company address with id: "+companyAddress.getId());
                CompanyAddress companyAddressFromDb = companyAddressRepository.findById(companyAddress.getId()).orElse(null);
                if(companyAddressFromDb!=null){

                    //update address if changed
                    if(!companyAddress.getAddress().equals(companyAddressFromDb.getAddress())){
                        companyAddressFromDb.setAddress(companyAddress.getAddress());
                        LatLng newCoordinates = null;
                        //Update coordinates
                        try {
                            newCoordinates = GoogleMapsCoordinates.getCoordinates(companyAddress.getAddress());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(null!=newCoordinates){
                            companyAddressFromDb.setLongitude(newCoordinates.lat);
                            companyAddressFromDb.setLongitude(newCoordinates.lng);
                        }
                        else{
                            errorResponse.put(companyAddress.getAddressTitle(), " Co-ordinates not updated");
                        }
                    }

                    //update address title if changed
                    if(!companyAddress.getAddressTitle().equals(companyAddressFromDb.getAddressTitle())) {
                        companyAddressFromDb.setAddressTitle(companyAddress.getAddressTitle());
                    }

                    //update address type if changed
                    if(!companyAddress.getAddressType().getId().equals(companyAddressFromDb.getAddressType().getId())){
                        companyAddressFromDb.setAddressType(companyAddress.getAddressType());
                    }

                    companyAddressFromDb.setUpdatedBy(loggedInUser.getId());
                    companyAddressFromDb.setUpdatedOn(new Date());

                    companyAddressRepository.save(companyAddressFromDb);
                    log.info("updated company address with id: "+companyAddress.getId());
                }
                else{
                    errorResponse.put(companyAddress.getAddressTitle(), "does not exist");
                }
            });
        }

        companyAddressRepository.flush();
        saveCompanyHistory(company.getId(), "Updated company Addresses", loggedInUser);

        if(errorResponse.size()>0) {
            log.info("Updated Company Addresses with errors: " + errorResponse);
            throw new WebException("Error while updating Addresses: "+errorResponse, HttpStatus.UNPROCESSABLE_ENTITY, errorResponse);
        }
        log.info("Company Addresses Updated for company Id:"+company.getId());
        company.setCompanyAddressList(companyAddressRepository.findByCompanyId(company.getId()));
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
        Company companyObjFromDb = companyRepository.findByCompanyNameIgnoreCase(company.getCompanyName());
        if(null == companyObjFromDb)
            throw new ValidationException("Company not found: " + company.getCompanyName(), HttpStatus.BAD_REQUEST);
        companyObjFromDb.setActive(!blockCompany);
        companyObjFromDb.setUpdatedOn(new Date());
        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        companyObjFromDb.setUpdatedBy(loggedInUser.getId());
        companyRepository.save(companyObjFromDb);
        saveCompanyHistory(companyObjFromDb.getId(), blockCompany ? "Unblocked":"Blocked", loggedInUser);
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

    /**
     *
     * Service method to fetch a list of all BUs of a company
     * @param companyName for which list to be fetched
     * @return List of BUs
     * @throws Exception
     */
    @Override
    public List<CompanyBu>getCompanyBuList(String companyName) throws Exception{
        log.info("Received request to get list of BUs for company: "+companyName);
        long startTime = System.currentTimeMillis();

        Company company = companyRepository.findByCompanyNameIgnoreCase(companyName);

        if(company==null)
            throw new WebException("No company found with name: "+companyName, HttpStatus.UNPROCESSABLE_ENTITY );

        log.info("Completed processing list of BUs for company: "+ companyName +" in " + (System.currentTimeMillis() - startTime) + "ms.");
        return company.getCompanyBuList();
    }

    @Override
    public List<CompanyAddress>getCompanyAddressesByType(String companyName, MasterData addressType)throws Exception{
        log.info("Received request to get list of Addresses for company: "+companyName);
        long startTime = System.currentTimeMillis();

        //find company by company Name
        Company company = companyRepository.findByCompanyNameIgnoreCase(companyName);

        //if company is null throw exception
        if(company==null)
            throw new WebException("No company found with name: "+companyName, HttpStatus.UNPROCESSABLE_ENTITY );

        //check if address type received belongs to addressType in master data, if not throw an Exception
        if(MasterDataBean.getInstance().getAddressType().get(addressType.getId())==null)
            throw new WebException("Invalid address type", HttpStatus.UNPROCESSABLE_ENTITY);

        //extract and collect addresses from company object whose addresstype id matches with the params addresstype id
        List<CompanyAddress> companyAddresses = company.getCompanyAddressList().stream()
                .filter(companyAddress -> {
                    return companyAddress.getAddressType().getId().equals(addressType.getId());
                }).collect(Collectors.toList());
        log.info("Completed processing list of BUs for company: "+ companyName +" in " + (System.currentTimeMillis() - startTime) + "ms.");
        return companyAddresses;
    }

    @Transactional
    public void saveCompanyHistory(Long companyId, String historyMsg, User loggedInUser) {
        companyHistoryRepository.save(new CompanyHistory(companyId, historyMsg, loggedInUser));
    }



}
