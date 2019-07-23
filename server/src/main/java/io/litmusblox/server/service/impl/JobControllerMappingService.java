/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.model.Candidate;
import io.litmusblox.server.model.CandidateScreeningQuestionResponse;
import io.litmusblox.server.model.JobCandidateMapping;
import io.litmusblox.server.model.JobScreeningQuestions;
import io.litmusblox.server.repository.CandidateScreeningQuestionResponseRepository;
import io.litmusblox.server.repository.JobCandidateMappingRepository;
import io.litmusblox.server.repository.JobScreeningQuestionsRepository;
import io.litmusblox.server.service.IJobControllerMappingService;
import io.litmusblox.server.service.UploadResponseBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation class for methods exposed by IJobControllerMappingService
 *
 * @author : Shital Raval
 * Date : 16/7/19
 * Time : 4:56 PM
 * Class Name : JobControllerMappingService
 * Project Name : server
 */
@Service
public class JobControllerMappingService implements IJobControllerMappingService {

    @Resource
    JobCandidateMappingRepository jobCandidateMappingRepository;

    @Resource
    CandidateScreeningQuestionResponseRepository candidateScreeningQuestionResponseRepository;

    @Resource
    JobScreeningQuestionsRepository jobScreeningQuestionsRepository;

    /**
     * Service method to add a individually added candidates to a job
     *
     * @param candidates the list of candidates to be added
     * @param jobId      the job for which the candidate is to be added
     * @return the status of upload operation
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UploadResponseBean uploadIndividualCandidate(List<Candidate> candidates, Long jobId) throws Exception {
        //TODO: Add relevant code here
        //for each candidate, check if it is new or old
        //for the combination of jobid + candidateId, check that a record doesnot exist in jcm
        //else, add a jcm row with stage = Source
        return null;
    }

    /**
     * Service method to add candidates from a file in one of the supported formats
     *
     * @param multipartFile the file with candidate information
     * @param jobId         the job for which the candidates have to be added
     * @param fileFormat    the format of file, for e.g. Naukri, LB format
     * @return the status of upload operation
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UploadResponseBean uploadCandidatesFromFile(MultipartFile multipartFile, Long jobId, String fileFormat) throws Exception {
        //TODO: Add relevant code here
        return null;
    }

    /**
     * Service method to source and add a candidate from a plugin, for example Naukri plugin
     *
     * @param candidate the candidate to be added
     * @param jobId     the job for which the candidate is to be added
     * @return the status of upload operation
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UploadResponseBean uploadCandidateFromPlugin(Candidate candidate, Long jobId) throws Exception {
        //TODO: Add relevant code here
        return null;
    }

    /**
     * Rest api to capture candidate consent from chatbot
     *
     * @param uuid     the uuid corresponding to a unique jcm record
     * @param interest boolean to capture candidate consent
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void captureCandidateInterest(UUID uuid, boolean interest) throws Exception {
        JobCandidateMapping objFromDb = jobCandidateMappingRepository.findByJcmUuid(uuid);
        if (null == objFromDb)
            throw new Exception("No mapping found for uuid " + uuid);
        objFromDb.setCandidateInterest(interest);
        objFromDb.setCandidateInterestDate(new Date());
        jobCandidateMappingRepository.save(objFromDb);
    }

    /**
     * Rest api to capture candidate response to screening questions from chatbot
     *
     * @param uuid              the uuid corresponding to a unique jcm record
     * @param candidateResponse the response provided by a candidate against each screening question
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveScreeningQuestionResponses(UUID uuid, Map<Long, String> candidateResponse) throws Exception {
        JobCandidateMapping objFromDb = jobCandidateMappingRepository.findByJcmUuid(uuid);
        if (null == objFromDb)
            throw new Exception("No mapping found for uuid " + uuid);

        candidateResponse.forEach((key,value) -> {
            candidateScreeningQuestionResponseRepository.save(new CandidateScreeningQuestionResponse(objFromDb.getId(),key, value));
        });
    }

    /**
     * Rest api to get all screening questions for the job
     *
     * @param uuid the uuid corresponding to a unique jcm record
     * @return the list of job screening questions
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public List<JobScreeningQuestions> getJobScreeningQuestions(UUID uuid) throws Exception {
        JobCandidateMapping objFromDb = jobCandidateMappingRepository.findByJcmUuid(uuid);
        if (null == objFromDb)
            throw new Exception("No mapping found for uuid " + uuid);

        return jobScreeningQuestionsRepository.findByJobId(objFromDb.getJob().getId());
    }
}
