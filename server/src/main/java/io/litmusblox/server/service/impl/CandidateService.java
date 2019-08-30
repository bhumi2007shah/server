/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.*;
import io.litmusblox.server.service.ICandidateService;
import io.litmusblox.server.utils.SentryUtil;
import io.litmusblox.server.utils.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for Candidate related operations
 *
 * @author : Shital Raval
 * Date : 24/7/19
 * Time : 6:10 PM
 * Class Name : CandidateService
 * Project Name : server
 */
@Log4j2
@Service
public class CandidateService implements ICandidateService {

    @Resource
    CandidateEmailHistoryRepository candidateEmailHistoryRepository;

    @Resource
    CandidateMobileHistoryRepository candidateMobileHistoryRepository;

    @Resource
    CandidateRepository candidateRepository;

    @Resource
    CandidateDetailsRepository candidateDetailsRepository;

    @Resource
    CandidateEducationDetailsRepository candidateEducationDetailsRepository;

    @Resource
    CandidateProjectDetailsRepository candidateProjectDetailsRepository;

    @Resource
    CandidateOnlineProfilesRepository candidateOnlineProfilesRepository;

    @Resource
    CandidateLanguageProficiencyRepository candidateLanguageProficiencyRepository;

    @Resource
    CandidateWorkAuthorizationRepository candidateWorkAuthorizationRepository;

    @Resource
    CandidateSkillDetailsRepository candidateSkillDetailsRepository;

    @Resource
    CandidateCompanyDetailsRepository candidateCompanyDetailsRepository;

    /**
     * Method to find a candidate using email or mobile number + country code
     *
     * @param email       the email of the candidate
     * @param mobile      the mobile number of the candidate
     * @param countryCode the country code for the mobile
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Candidate findByMobileOrEmail(String email, String mobile, String countryCode) throws Exception {
        //check if candidate exists for email
        Candidate dupCandidateByEmail = null;
        CandidateEmailHistory candidateEmailHistory = candidateEmailHistoryRepository.findByEmail(email);
        if (null != candidateEmailHistory)
            dupCandidateByEmail = candidateEmailHistory.getCandidate();

        //check if candidate exists for mobile
        Candidate dupCandidateByMobile = null;
        if (Util.isNotNull(mobile)) {
            CandidateMobileHistory candidateMobileHistory = candidateMobileHistoryRepository.findByMobileAndCountryCode(mobile, countryCode);
            if (null != candidateMobileHistory)
                dupCandidateByMobile = candidateMobileHistory.getCandidate();
        }

        if(null != dupCandidateByEmail && null != dupCandidateByMobile) {
            //found different candidate ids for the email and mobile number combination
            if(!dupCandidateByEmail.getId().equals(dupCandidateByMobile.getId()))
                throw new ValidationException(IErrorMessages.CANDIDATE_ID_MISMATCH_FROM_HISTORY + mobile + " " + email);
        }
        else {

            User loggedInUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (null == dupCandidateByEmail && null != dupCandidateByMobile) {
                //Candidate by mobile exists, add email history
                candidateEmailHistoryRepository.save(new CandidateEmailHistory(dupCandidateByMobile, email, new Date(), loggedInUser));
                return dupCandidateByMobile;
            }
            if (null != dupCandidateByEmail && null == dupCandidateByMobile && Util.isNotNull(mobile)) {
                //Candidate by email exists, add mobile history
                candidateMobileHistoryRepository.save(new CandidateMobileHistory(dupCandidateByEmail, mobile, countryCode, new Date(), loggedInUser));
                return dupCandidateByEmail;
            }
        }
        return dupCandidateByEmail;
    }

    /**
     * Method to create a new candidate, candidateEmailHistory and candidateMobileHistory
     *
     * @param firstName    first name of candidate
     * @param lastName     last name of candidate
     * @param email        email of candidate
     * @param mobile       mobile number of candidate
     * @param countryCode  country code of candidate
     * @param loggedInUser
     * @return
     */
    @Override
    public Candidate createCandidate(String firstName, String lastName, String email, String mobile, String countryCode, User loggedInUser) throws Exception {

        Candidate candidate = candidateRepository.save(new Candidate(firstName, lastName, email, mobile, countryCode, new Date(), loggedInUser));
        candidateEmailHistoryRepository.save(new CandidateEmailHistory(candidate, email, new Date(), loggedInUser));
        candidateMobileHistoryRepository.save(new CandidateMobileHistory(candidate, mobile, countryCode, new Date(), loggedInUser));

        return candidate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CandidateDetails saveUpdateCandidateDetails(CandidateDetails candidateDetails, Candidate candidate){
        //delete from CandidateDetails

        candidateDetailsRepository.deleteByCandidateId(candidate);

        if(!Util.isNull(candidateDetails.getCurrentAddress()) && candidateDetails.getCurrentAddress().length() > IConstant.MAX_FIELD_LENGTHS.ADDRESS.getValue()) {
            candidateDetails.setCurrentAddress(truncateField(candidate.getId().toString(), IConstant.MAX_FIELD_LENGTHS.ADDRESS.name(), IConstant.MAX_FIELD_LENGTHS.ADDRESS.getValue(), candidateDetails.getCurrentAddress()));
        }
        if(!Util.isNull(candidateDetails.getKeySkills()) && candidateDetails.getKeySkills().length() > IConstant.MAX_FIELD_LENGTHS.KEY_SKILLS.getValue()) {
            candidateDetails.setKeySkills(truncateField(candidate.getId().toString(), IConstant.MAX_FIELD_LENGTHS.KEY_SKILLS.name(), IConstant.MAX_FIELD_LENGTHS.KEY_SKILLS.getValue(), candidateDetails.getKeySkills()));
        }
        if(!Util.isNull(candidateDetails.getWorkSummary()) && candidateDetails.getWorkSummary().length() > IConstant.MAX_FIELD_LENGTHS.WORK_SUMMARY.getValue()) {
            candidateDetails.setWorkSummary(truncateField(candidate.getId().toString(), IConstant.MAX_FIELD_LENGTHS.WORK_SUMMARY.name(), IConstant.MAX_FIELD_LENGTHS.WORK_SUMMARY.getValue(), candidateDetails.getWorkSummary()));
        }
        candidateDetails.setCandidateId(candidate);
        candidateDetailsRepository.save(candidateDetails);
        return candidateDetails;
    }

    @Override
    @Transactional
    public void saveUpdateCandidateEducationDetails(List<CandidateEducationDetails> candidateEducationDetails, Long candidateId) throws Exception {
        //delete existing records
        candidateEducationDetailsRepository.deleteByCandidateId(candidateId);
        //insert new ones
        candidateEducationDetails.forEach(obj -> {
            //check if institute name is more than 75 characters
            if (!Util.isNull(obj.getInstituteName()) && obj.getInstituteName().length() > IConstant.MAX_FIELD_LENGTHS.INSTITUTE_NAME.getValue()){
                obj.setInstituteName(truncateField(candidateId.toString(), IConstant.MAX_FIELD_LENGTHS.INSTITUTE_NAME.name(), IConstant.MAX_FIELD_LENGTHS.INSTITUTE_NAME.getValue(), obj.getInstituteName()));
            }
            obj.setCandidateId(candidateId);
            candidateEducationDetailsRepository.save(obj);});
    }

    @Transactional
    @Override
    public void saveUpdateCandidateProjectDetails(List<CandidateProjectDetails> candidateProjectDetails, Long candidateId) throws Exception {
        //delete existing records
        candidateProjectDetailsRepository.deleteByCandidateId(candidateId);
        //insert new ones
        candidateProjectDetails.forEach(obj -> {obj.setCandidateId(candidateId);candidateProjectDetailsRepository.save(obj);});
    }

    @Transactional
    @Override
    public void saveUpdateCandidateOnlineProfile(List<CandidateOnlineProfile> candidateOnlineProfiles, Long candidateId) throws Exception {
        //delete existing records
        candidateOnlineProfilesRepository.deleteByCandidateId(candidateId);
        //insert new ones
        candidateOnlineProfiles.forEach(obj -> {
            if(!Util.isNull(obj.getUrl()) && obj.getUrl().length() > IConstant.MAX_FIELD_LENGTHS.ONLINE_PROFILE_URL.getValue()) {
                obj.setUrl(truncateField(candidateId.toString(), IConstant.MAX_FIELD_LENGTHS.ONLINE_PROFILE_URL.name(), IConstant.MAX_FIELD_LENGTHS.ONLINE_PROFILE_URL.getValue(), obj.getUrl()));
            }
            obj.setCandidateId(candidateId);candidateOnlineProfilesRepository.save(obj);});
    }

    @Transactional
    @Override
    public void saveUpdateCandidateLanguageProficiency(List<CandidateLanguageProficiency> candidateLanguageProficiencies, Long candidateId) throws Exception {
        //delete existing records
        candidateLanguageProficiencyRepository.deleteByCandidateId(candidateId);
        //insert new ones
        candidateLanguageProficiencies.forEach(obj -> {obj.setCandidateId(candidateId);candidateLanguageProficiencyRepository.save(obj);});
    }

    @Transactional
    public void saveUpdateCandidateWorkAuthorization(List<CandidateWorkAuthorization> candidateWorkAuthorizations, Long candidateId) throws Exception {
        //delete existing records
        candidateWorkAuthorizationRepository.deleteByCandidateId(candidateId);
        //insert new ones
        candidateWorkAuthorizations.forEach(obj -> {obj.setCandidateId(candidateId);candidateWorkAuthorizationRepository.save(obj);});
    }

    @Transactional
    public void saveUpdateCandidateSkillDetails(List<CandidateSkillDetails> candidateSkillDetails, Long candidateId) throws Exception {
        //delete existing records
        candidateSkillDetailsRepository.deleteByCandidateId(candidateId);
        //insert new ones
        candidateSkillDetails.forEach(obj -> {obj.setCandidateId(candidateId);candidateSkillDetailsRepository.save(obj);});

    }

    @Transactional
    public void saveUpdateCandidateCompanyDetails(List<CandidateCompanyDetails> candidateCompanyDetails, Long candidateId) throws Exception {
        //delete existing records
        candidateCompanyDetailsRepository.deleteByCandidateId(candidateId);

        //insert new ones
        candidateCompanyDetails.forEach(obj -> {
            obj.setCandidateId(candidateId);
            //Check for company name
            if (!Util.isNull(obj.getCompanyName()) && obj.getCompanyName().length() > IConstant.MAX_FIELD_LENGTHS.COMPANY_NAME.getValue()) {
                //truncate institute name to max length
                obj.setCompanyName(truncateField(candidateId.toString(), IConstant.MAX_FIELD_LENGTHS.COMPANY_NAME.name(), IConstant.MAX_FIELD_LENGTHS.COMPANY_NAME.getValue(), obj.getCompanyName()));
            }
            //check for designation
            if (!Util.isNull(obj.getDesignation()) && obj.getDesignation().length() > IConstant.MAX_FIELD_LENGTHS.DESIGNATION.getValue()) {
                obj.setDesignation(truncateField(candidateId.toString(), IConstant.MAX_FIELD_LENGTHS.DESIGNATION.name(), IConstant.MAX_FIELD_LENGTHS.DESIGNATION.getValue(), obj.getDesignation()));
            }
            candidateCompanyDetailsRepository.save(obj);});
    }

    //Method to truncate the value in the field and send out a sentry message for the same
    private String truncateField(String candidateId, String fieldName, int fieldLength, String fieldValue) {
        StringBuffer info = new StringBuffer(fieldName).append(" is longer than the permitted length of ").append(fieldLength).append(" ").append(fieldValue);
        log.info(info.toString());
        Map<String, String> breadCrumb = new HashMap<>();
        breadCrumb.put("Candidate Id",candidateId);
        breadCrumb.put(fieldName, fieldValue);
        SentryUtil.logWithStaticAPI(null, info.toString(), breadCrumb);
        return fieldValue.substring(0, fieldLength);
    }

}
