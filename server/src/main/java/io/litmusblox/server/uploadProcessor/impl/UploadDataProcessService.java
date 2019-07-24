/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor.impl;

import io.litmusblox.server.Util;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.model.Candidate;
import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobCandidateMapping;
import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.CandidateRepository;
import io.litmusblox.server.repository.JobCandidateMappingRepository;
import io.litmusblox.server.repository.JobRepository;
import io.litmusblox.server.service.ICandidateService;
import io.litmusblox.server.service.MasterDataBean;
import io.litmusblox.server.service.UploadResponseBean;
import io.litmusblox.server.uploadProcessor.IUploadDataProcessService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

    @Resource
    JobRepository jobRepository;

    @Resource
    CandidateRepository candidateRepository;

    @Resource
    JobCandidateMappingRepository jobCandidateMappingRepository;

    @Autowired
    ICandidateService candidateService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processData(List<Candidate> candidateList, UploadResponseBean uploadResponseBean, int candidateProcessed, Long jobId, boolean ignoreMobile){
        log.info("inside processData");

        int recordsProcessed = 0;
        int successCount = 0;
        int failureCount = uploadResponseBean.getFailureCount();

        User loggedInUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Job job=jobRepository.getOne(jobId);

        for (Candidate candidate:candidateList) {

            if(recordsProcessed >= Integer.parseInt(environment.getProperty(IConstant.MAX_CANDIDATES_PER_FILE))) {
                log.error(IErrorMessages.MAX_CANDIDATE_PER_FILE_EXCEEDED + " : user id : " +  candidate.getCreatedBy().getId());
                candidate.setUploadErrorMessage(IErrorMessages.MAX_CANDIDATE_PER_FILE_EXCEEDED + ". Max number of " +
                        "candidates per file is "+environment.getProperty("maxCandidatesPerFile")+". All candidates from this candidate onwards have not been processed");
                uploadResponseBean.getFailedCandidates().add(candidate);
                failureCount++;
                break;
            }
            //check for daily limit per user
            if ((recordsProcessed + candidateProcessed) >= Integer.parseInt(environment.getProperty(IConstant.MAX_CANDIDATES_PER_USER_PER_DAY))) {
                log.error(IErrorMessages.MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED  + " : user id : " +  candidate.getCreatedBy().getId());
                candidate.setUploadErrorMessage(IErrorMessages.MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED);
                uploadResponseBean.getFailedCandidates().add(candidate);
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
                    if (!Util.validateMobile(candidate.getMobile(), loggedInUser.getCountryId().getCountryCode())) {
                        String cleanMobile = candidate.getMobile().replaceAll(IConstant.REGEX_TO_CLEAR_SPECIAL_CHARACTERS_FOR_MOBILE, "");
                        log.error("Special characters found, cleaning mobile number \"" + candidate.getMobile() + "\" to " + cleanMobile);
                        if (!Util.validateMobile(cleanMobile, candidate.getCountryCode()))
                            throw new ValidationException(IErrorMessages.MOBILE_INVALID_DATA + " - " + candidate.getMobile(), HttpStatus.BAD_REQUEST);
                        candidate.setMobile(cleanMobile);
                    }
                    msg.append("-").append(candidate.getMobile());
                }else {
                    //mobile number of candidate is null
                    //check if ignore mobile flag is set
                    if(ignoreMobile) {
                        log.info("Ignoring check for mobile number required for " + candidate.getEmail());
                    }
                    else {
                        //ignore mobile flag is false, throw an exception
                        throw new ValidationException(IErrorMessages.MOBILE_NULL_OR_BLANK + " - " + candidate.getMobile(), HttpStatus.BAD_REQUEST);
                    }

                }
                log.info(msg);

                //create a candidate if no history found for email and mobile
                long candidateId;
                Candidate existingCandidate = candidateService.findByMobileOrEmail(candidate.getEmail(),candidate.getMobile(),(Util.isNull(candidate.getCountryCode())?loggedInUser.getCountryId().getCountryCode():candidate.getCountryCode()));
                Candidate candidateObjToUse = existingCandidate;
                if(null == existingCandidate) {
                    candidate.setCreatedOn(new Date());
                    candidate.setCreatedBy(loggedInUser);
                    if(Util.isNull(candidate.getCountryCode()))
                        candidate.setCountryCode(loggedInUser.getCountryId().getCountryCode());
                    candidateObjToUse = candidateService.createCandidate(candidate.getFirstName(), candidate.getLastName(), candidate.getEmail(), candidate.getMobile(), candidate.getCountryCode(), loggedInUser);
                    msg.append(" New");
                }
                else {
                    log.info("Found existing candidate: " + existingCandidate.getId());
                }

                log.info(msg);

                //find duplicate candidate for job
                JobCandidateMapping jobCandidateMapping = jobCandidateMappingRepository.findByJobAndCandidate(job, candidateObjToUse);

                if(null!=jobCandidateMapping){
                    log.error(IErrorMessages.DUPLICATE_CANDIDATE + " : " + candidateObjToUse.getId() + candidate.getEmail() + " : " + candidate.getMobile());
                    candidate.setUploadErrorMessage(IErrorMessages.DUPLICATE_CANDIDATE);
                    throw new ValidationException(IErrorMessages.DUPLICATE_CANDIDATE + " - " +"JobId:"+jobId, HttpStatus.BAD_REQUEST);
                }else{
                    //Create new entry for JobCandidateMapping
                    jobCandidateMappingRepository.save(new JobCandidateMapping(job,candidateObjToUse,MasterDataBean.getInstance().getSourceStage(), candidate.getCandidateSource(),new Date(),loggedInUser, UUID.randomUUID()));
                }

                successCount++;
            }catch(ValidationException ve) {
                log.error("Error while processing candidate : " + candidate.getEmail() + " : " + ve.getErrorMessage(), HttpStatus.BAD_REQUEST);
                candidate.setUploadErrorMessage(ve.getErrorMessage());
                uploadResponseBean.getFailedCandidates().add(candidate);
                failureCount++;
            } catch(Exception e) {
                e.printStackTrace();
                log.error("Error while processing candidate : " + candidate.getEmail() + " : " + e.getMessage(), HttpStatus.BAD_REQUEST);
                candidate.setUploadErrorMessage(IErrorMessages.INTERNAL_SERVER_ERROR);
                uploadResponseBean.getFailedCandidates().add(candidate);
                failureCount++;
            }
        }

        uploadResponseBean.setFailureCount(failureCount);
        uploadResponseBean.setSuccessCount(successCount);

        if(uploadResponseBean.getFailureCount() == 0)
            uploadResponseBean.setStatus(IConstant.UPLOAD_STATUS.Success.name());
        else if(uploadResponseBean.getSuccessCount() == 0)
            uploadResponseBean.setStatus(IConstant.UPLOAD_STATUS.Failure.name());
        else
            uploadResponseBean.setStatus(IConstant.UPLOAD_STATUS.Partial_Success.name());

        uploadResponseBean.setCandidatesProcessedCount(candidateProcessed + successCount);

    }
}
