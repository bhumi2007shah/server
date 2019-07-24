/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import io.litmusblox.server.model.Candidate;
import io.litmusblox.server.model.User;

/**
 * Service class for operations on candidate
 *
 * @author : Shital Raval
 * Date : 24/7/19
 * Time : 6:09 PM
 * Class Name : ICandidateService
 * Project Name : server
 */
public interface ICandidateService {
    /**
     * Method to find a candidate using email or mobile number + country code
     * @param email the email of the candidate
     * @param mobile the mobile number of the candidate
     * @param countryCode the country code for the mobile
     * @return
     * @throws Exception
     */
    Candidate findByMobileOrEmail(String email, String mobile, String countryCode) throws Exception;

    /**
     * Method to create a new candidate, candidateEmailHistory and candidateMobileHistory
     *
     * @param firstName first name of candidate
     * @param lastName last name of candidate
     * @param email email of candidate
     * @param mobile mobile number of candidate
     * @param countryCode country code of candidate
     * @param loggedInUser
     * @return
     */
    Candidate createCandidate(String firstName, String lastName, String email, String mobile, String countryCode, User loggedInUser) throws Exception;
}
