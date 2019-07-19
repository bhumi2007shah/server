/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor.impl;

import io.litmusblox.server.Util;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.model.Candidate;
import io.litmusblox.server.service.UploadResponseBean;
import io.litmusblox.server.uploadProcessor.IUploadDataProcessService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Sumit
 * Date : 17/7/19
 * Time : 2:49 PM
 * Class Name : UploadDataProcessService
 * Project Name : server
 */
@PropertySource("classpath:appConfig.properties")
@Service
@Log4j2
public class UploadDataProcessService implements IUploadDataProcessService {

    @Autowired
    Environment environment;


    @Override
    public void processData(List<Candidate> candidateList, UploadResponseBean uploadResponseBean, int candidateProcessed){
        log.info("inside processData");

        int recordsProcessed = 0;
        int successCount = 0;
        int failureCount = uploadResponseBean.getFailureCount();
        List<Candidate> failedCandidateList = new ArrayList();

        for (Candidate candidate:candidateList) {

            if(recordsProcessed >= Integer.parseInt(environment.getProperty(IConstant.MAX_CANDIDATES_PER_FILE))) {
                log.error(IErrorMessages.MAX_CANDIDATE_PER_FILE_EXCEEDED + " : user id : " +  candidate.getCreatedBy().getId());
                candidate.setUploadErrorMessage(IErrorMessages.MAX_CANDIDATE_PER_FILE_EXCEEDED + ". Max number of " +
                        "candidates per file is "+environment.getProperty("maxCandidatesPerFile")+". All candidates from this candidate onwards have not been processed");
                failedCandidateList.add(candidate);
                failureCount++;
                break;
            }
            //check for daily limit per user
            if ((recordsProcessed + candidateProcessed) >= Integer.parseInt(environment.getProperty(IConstant.MAX_CANDIDATES_PER_USER_PER_DAY))) {
                log.error(IErrorMessages.MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED  + " : user id : " +  candidate.getCreatedBy().getId());
                candidate.setUploadErrorMessage(IErrorMessages.MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED);
                failedCandidateList.add(candidate);
                failureCount++;
                break;
            }


            try {

                recordsProcessed++;

                if (null != candidate.getFirstName()) {
                    if (!Util.validateName(candidate.getFirstName().trim())) {
                        String cleanFirstName = candidate.getFirstName().replaceAll(IConstant.REGEX_TO_CLEAR_SPECIAL_CHARACTERS_FOR_NAME, "");
                        log.error("Special characters found, cleaning First name \"" + candidate.getFirstName() + "\" to " + cleanFirstName);
                        if (!Util.validateName(cleanFirstName))
                            throw new ValidationException(IErrorMessages.NAME_FIELD_SPECIAL_CHARACTERS + " - " + candidate.getFirstName(), HttpStatus.BAD_REQUEST);
                        candidate.setFirstName(cleanFirstName);
                    }
                }

                if (null != candidate.getLastName()) {
                    if (!Util.validateName(candidate.getLastName().trim())) {
                        String cleanLastName = candidate.getLastName().replaceAll(IConstant.REGEX_TO_CLEAR_SPECIAL_CHARACTERS_FOR_NAME, "");
                        log.error("Special characters found, cleaning Last name \"" + candidate.getLastName() + "\" to " + cleanLastName);
                        if (!Util.validateName(cleanLastName))
                            throw new ValidationException(IErrorMessages.NAME_FIELD_SPECIAL_CHARACTERS + " - " + candidate.getLastName(), HttpStatus.BAD_REQUEST);
                        candidate.setLastName(cleanLastName);
                    }
                }

                if (!Util.validateEmail(candidate.getEmail())) {
                    String cleanEmail = candidate.getEmail().replaceAll(IConstant.REGEX_TO_CLEAR_SPECIAL_CHARACTERS_FOR_EMAIL,"");
                    log.error("Special characters found, cleaning Email \"" + candidate.getEmail() + "\" to " + cleanEmail);
                    if (!Util.validateEmail(cleanEmail)) {
                        throw new ValidationException(IErrorMessages.INVALID_EMAIL + " - " + candidate.getEmail(), HttpStatus.BAD_REQUEST);
                    }
                    candidate.setEmail(cleanEmail);
                }

                StringBuffer msg = new  StringBuffer(candidate.getFirstName()).append(" ").append(candidate.getLastName()).append(" ~ ").append(candidate.getEmail());

                if(Util.isNotNull(candidate.getMobile())) {
                    if (!Util.validateMobile(candidate.getMobile(), candidate.getCountryCode())) {
                        String cleanMobile = candidate.getMobile().replaceAll(IConstant.REGEX_TO_CLEAR_SPECIAL_CHARACTERS_FOR_MOBILE, "");
                        log.error("Special characters found, cleaning mobile number \"" + candidate.getMobile() + "\" to " + cleanMobile);
                        if (!Util.validateMobile(cleanMobile, candidate.getCountryCode()))
                            throw new ValidationException(IErrorMessages.MOBILE_INVALID_DATA + " - " + candidate.getMobile(), HttpStatus.BAD_REQUEST);
                        candidate.setMobile(cleanMobile);
                    }
                    msg.append("-").append(candidate.getMobile());
                }

                successCount++;
                log.info(msg);

            }catch(ValidationException ve) {
                log.error("Error while processing candidate : " + candidate.getEmail() + " : " + ve.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
                candidate.setUploadErrorMessage(ve.getLocalizedMessage());
                failedCandidateList.add(candidate);
                failureCount++;
            } catch(Exception e) {
                log.error("Error while processing candidate : " + candidate.getEmail() + " : " + e.getMessage(), HttpStatus.BAD_REQUEST);
                candidate.setUploadErrorMessage(IErrorMessages.INTERNAL_SERVER_ERROR);
                failedCandidateList.add(candidate);
                failureCount++;
            }
        }

        uploadResponseBean.setFailureCount(failureCount);
        uploadResponseBean.setSuccessCount(successCount);
        if(failedCandidateList.size()>0){
            uploadResponseBean.getFailedCandidates().addAll(failedCandidateList);
        }
    }

}
