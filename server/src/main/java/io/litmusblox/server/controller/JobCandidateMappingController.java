/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.model.Candidate;
import io.litmusblox.server.model.JobCandidateMapping;
import io.litmusblox.server.repository.UserRepository;
import io.litmusblox.server.service.CvUploadResponseBean;
import io.litmusblox.server.service.IJobCandidateMappingService;
import io.litmusblox.server.service.ShareCandidateProfileRequestBean;
import io.litmusblox.server.service.UploadResponseBean;
import io.litmusblox.server.uploadProcessor.IProcessUploadedCV;
import io.litmusblox.server.utils.Util;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Controller class for following:
 * 1. Upload single candidate for a job
 * 2. Upload an excel file of candidates for a job
 *
 * @author : Shital Raval
 * Date : 16/7/19
 * Time : 4:39 PM
 * Class Name : JobCandidateMappingController
 * Project Name : server
 */
@CrossOrigin(origins = "*", methods = {RequestMethod.POST, RequestMethod.GET, RequestMethod.OPTIONS}, allowedHeaders = {"Content-Type", "Authorization","X-Requested-With", "accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"}, exposedHeaders = {"Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"})
@RestController
@RequestMapping("/api/jcm")
@Log4j2
public class JobCandidateMappingController {

    @Autowired
    IJobCandidateMappingService jobCandidateMappingService;

    @Autowired
    IProcessUploadedCV processUploadedCV;

    @Autowired
    UserRepository userRepository;

    /**
     * Api to add a single candidate to a job
     *
     * @param candidate the candidate to be added
     * @param jobId the job id for which the candidate is to be added
     * @throws Exception
     */
    @PostMapping(value = "/addCandidate/individual")
    @ResponseStatus(value = HttpStatus.OK)
    String addSingleCandidate(@RequestBody List<Candidate> candidate, @RequestParam("jobId") Long jobId) throws Exception{
        log.info("Received request to add a list of individually added candidates. Number of candidates to be added: " + candidate.size());
        log.info("Candidate name: " + candidate.get(0).getFirstName()+" "+candidate.get(0).getLastName());
        long startTime = System.currentTimeMillis();
        UploadResponseBean responseBean = jobCandidateMappingService.uploadIndividualCandidate(candidate, jobId);
        log.info("Completed processing list of candidates in " + (System.currentTimeMillis()-startTime) + "ms.");
        return Util.stripExtraInfoFromResponseBean(responseBean, null,
                new HashMap<String, List<String>>() {{
                    put("Candidate", Arrays.asList("candidateDetails","candidateEducationDetails","candidateProjectDetails","candidateCompanyDetails",
                            "candidateOnlineProfiles","candidateWorkAuthorizations","candidateLanguageProficiencies","candidateSkillDetails"));
                }});
    }

    /**
     * Api method to add candidates from a file in one of the supported formats
     *
     * @param multipartFile the file with candidate information
     * @param jobId the job for which the candidates have to be added
     * @param fileFormat the format of file, for e.g. Naukri, LB format
     * @return the status of upload operation
     * @throws Exception
     */
    @PostMapping(value = "/addCandidate/file")
    @ResponseStatus(value = HttpStatus.OK)
    String addCandidatesFromFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam("jobId")Long jobId, @RequestParam("fileFormat")String fileFormat) throws Exception {
        log.info("Received request to add candidates from a file.");
        long startTime = System.currentTimeMillis();
        UploadResponseBean responseBean = jobCandidateMappingService.uploadCandidatesFromFile(multipartFile, jobId, fileFormat);
        log.info("Completed processing candidates from file in " + (System.currentTimeMillis()-startTime) + "ms.");
        return Util.stripExtraInfoFromResponseBean(responseBean, null,
                new HashMap<String, List<String>>() {{
                    put("Candidate", Arrays.asList("candidateDetails","candidateEducationDetails","candidateProjectDetails","candidateCompanyDetails",
                            "candidateOnlineProfiles","candidateWorkAuthorizations","candidateLanguageProficiencies","candidateSkillDetails"));
                    put("User", Arrays.asList("createdBy","company"));
                }});
    }

    /**
     * Api method to source and add a candidate from a plugin, for example Naukri plugin
     *
     * @param candidateString the json string of candidate to be added
     * @param jobId the job for which the candidate is to be added
     * @return the status of upload operation
     * @throws Exception
     */
    @PostMapping(value = "/addCandidate/plugin")
    String uploadCandidateFromPlugin(@RequestParam(name = "candidateCv", required = false) MultipartFile candidateCv, @RequestParam("candidate") String candidateString, @RequestParam("jobId") Long jobId) throws Exception {
        log.info("Received request to add a candidate from plugin");
        long startTime = System.currentTimeMillis();
        Candidate candidate=new ObjectMapper().readValue(candidateString, Candidate.class);
        UploadResponseBean responseBean = jobCandidateMappingService.uploadCandidateFromPlugin(candidate, jobId, candidateCv);
        log.info("Completed adding candidate from plugin in " + (System.currentTimeMillis()-startTime) + "ms.");
        return Util.stripExtraInfoFromResponseBean(responseBean, null,
                new HashMap<String, List<String>>() {{
                    put("Candidate", Arrays.asList("candidateDetails","candidateEducationDetails","candidateProjectDetails","candidateCompanyDetails",
                            "candidateOnlineProfiles","candidateWorkAuthorizations","candidateLanguageProficiencies","candidateSkillDetails"));
                }});
    }

    /**
     * Api to invite candidates to fill chatbot for a job
     *
     * @param jcmList list of jcm ids for chatbot invitation
     * @throws Exception
     */
    @PostMapping(value = "/inviteCandidates")
    @ResponseStatus(value = HttpStatus.OK)
    void inviteCandidates(@RequestBody List<Long> jcmList) throws Exception {
        log.info("Received request to invite candidates");
        long startTime = System.currentTimeMillis();
        jobCandidateMappingService.inviteCandidates(jcmList);
        log.info("Completed inviting candidates in " + (System.currentTimeMillis()-startTime)+"ms.");
    }

    /**
     * REST Api to process sharing of candidate profiles with Hiring managers
     *
     * @param requestBean The request bean with information about the profile to be shared, the recepient name and recepient email address
     * @throws Exception
     */
    @PostMapping(value = "/shareProfile")
    @ResponseStatus(value = HttpStatus.OK)
    void shareCandidateProfile(@RequestBody ShareCandidateProfileRequestBean requestBean) {
        log.info("Received request to share candidate profile with hiring managers");
        long startTime = System.currentTimeMillis();
        jobCandidateMappingService.shareCandidateProfiles(requestBean);
        log.info("Completed processing share candidate profile request in " + (System.currentTimeMillis()-startTime) + "ms.");
    }


    /**
     * REST Api to fetch details of a single candidate for a job
     *
     * @param jobCandidateMappingId
     * @return candidate object as json
     * @throws Exception
     */
    @GetMapping("/fetchCandidateProfile/{jobCandidateMappingId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    String getCandidateProfile(@PathVariable("jobCandidateMappingId") Long jobCandidateMappingId) throws Exception {
        log.info("Received request to fetch candidate profile");
        long startTime = System.currentTimeMillis();
        String response = Util.stripExtraInfoFromResponseBean(jobCandidateMappingService.getCandidateProfile(jobCandidateMappingId),
                new HashMap<String, List<String>>() {{
                    put("User", Arrays.asList("displayName"));
                    put("ScreeningQuestions", Arrays.asList("question"));
                    put("JobCandidateMapping", Arrays.asList("displayName"));
                }},
                new HashMap<String, List<String>>() {{
                    put("Candidate",Arrays.asList("id","createdBy","createdOn","updatedBy","updatedOn","uploadErrorMessage", "firstName", "lastName"));
                    put("CompanyScreeningQuestion", Arrays.asList("createdOn", "createdBy", "updatedOn", "updatedBy","company", "questionType"));
                    put("UserScreeningQuestion", Arrays.asList("createdOn","createdBy","updatedOn","userId","questionType"));
                    put("JobCandidateMapping", Arrays.asList("createdOn","createdBy","updatedOn","updatedBy"));
                    put("CandidateDetails", Arrays.asList("id","candidateId"));
                    put("CandidateEducationDetails", Arrays.asList("id","candidateId"));
                    put("CandidateLanguageProficiency", Arrays.asList("id","candidateId"));
                    put("CandidateOnlineProfile", Arrays.asList("id","candidateId"));
                    put("CandidateProjectDetails", Arrays.asList("id","candidateId"));
                    put("CandidateCompanyDetails", Arrays.asList("id","candidateId"));
                    put("CandidateSkillDetails", Arrays.asList("id","candidateId"));
                    put("CandidateWorkAuthorization", Arrays.asList("id","candidateId"));
                    put("JobScreeningQuestions", Arrays.asList("id","jobId","createdBy", "createdOn", "updatedOn","updatedBy"));
                }});
        log.info("Completed processing fetch candidate profile request in " + (System.currentTimeMillis()-startTime) + "ms.");
        return response;
    }


    /**
     * Api to upload candidates by means of drag and drop cv
     *
     * @param multipartFiles files to be processed to upload candidates
     * @param jobId the job for which the candidate is to be added
     * @return response bean with details about success / failure of each candidate file
     * @throws Exception
     */
    @PostMapping("/addCandidate/dragAndDropCv")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    CvUploadResponseBean dragAndDropCV(@RequestParam("files") MultipartFile[] multipartFiles, @RequestParam("jobId")Long jobId) throws Exception {
        return jobCandidateMappingService.processDragAndDropCv(multipartFiles, jobId);
    }

    /**
     * Api to update Jcm and also candidate
     *
     * @param jobCandidateMapping updated jcm object
     */
    @PostMapping("/updateJcm")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    void updateJcm(@RequestBody JobCandidateMapping jobCandidateMapping){
        jobCandidateMappingService.updateJcm(jobCandidateMapping);
    }
}