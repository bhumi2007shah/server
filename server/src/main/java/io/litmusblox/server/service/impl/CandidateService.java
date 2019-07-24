/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.Util;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.model.Candidate;
import io.litmusblox.server.model.CandidateEmailHistory;
import io.litmusblox.server.model.CandidateMobileHistory;
import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.CandidateEmailHistoryRepository;
import io.litmusblox.server.repository.CandidateMobileHistoryRepository;
import io.litmusblox.server.repository.CandidateRepository;
import io.litmusblox.server.service.ICandidateService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Service class for Candidate related operations
 *
 * @author : Shital Raval
 * Date : 24/7/19
 * Time : 6:10 PM
 * Class Name : CandidateService
 * Project Name : server
 */
@Service
public class CandidateService implements ICandidateService {

    @Resource
    CandidateEmailHistoryRepository candidateEmailHistoryRepository;

    @Resource
    CandidateMobileHistoryRepository candidateMobileHistoryRepository;

    @Resource
    CandidateRepository candidateRepository;

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
                throw new Exception(IErrorMessages.CANDIDATE_ID_MISMATCH_FROM_HISTORY + mobile + " " + email);
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
}
