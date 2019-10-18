/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */
package io.litmusblox.server.controller;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobCandidateMapping;
import io.litmusblox.server.service.IJobService;
import io.litmusblox.server.service.SingleJobViewResponseBean;
import io.litmusblox.server.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller class that exposes all REST endpoints for Job related operations
 *
 * @author : Shital Raval
 * Date : 1/7/19
 * Time : 2:09 PM
 * Class Name : JobController
 * Project Name : server
 */
@CrossOrigin(allowedHeaders = "*")
@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    IJobService jobService;

    @PostMapping(value = "/createJob/{pageName}")
    String addJob(@RequestBody String jobStr, @PathVariable ("pageName") String pageName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        Job job = mapper.readValue(jobStr, Job.class);

        return Util.stripExtraInfoFromResponseBean(
            jobService.addJob(job, pageName),
            (new HashMap<String, List<String>>(){{
                put("User",Arrays.asList("displayName","id"));
                put("ScreeningQuestions", Arrays.asList("question","id"));
            }}),
            (new HashMap<String, List<String>>(){{
                put("Job",Arrays.asList("createdOn","createdBy", "updatedOn", "updatedBy"));
                put("CompanyScreeningQuestion", Arrays.asList("createdOn", "createdBy", "updatedOn", "updatedBy","company"));
                put("UserScreeningQuestion", Arrays.asList("createdOn", "updatedOn","userId"));
                put("JobScreeningQuestions", Arrays.asList("id","jobId","createdBy", "createdOn", "updatedOn","updatedBy"));
            }})
        );
    }

    /**
     * Api for retrieving a list of jobs created by user
     * @param archived optional flag indicating if a list of archived jobs is requested. By default only open jobs will be returned
     * @param companyName optional name of the company for which jobs have to be found. Will be populated only when superadmin accesses an account
     * @return response bean with a list of jobs, count of open jobs and count of archived jobs
     * @throws Exception
     */
    @GetMapping(value = "/listOfJobs")
    String listAllJobsForUser(@RequestParam("archived") Optional<Boolean> archived, @RequestParam("companyName") Optional<String> companyName) throws Exception {

        return Util.stripExtraInfoFromResponseBean(
                jobService.findAllJobsForUser((archived.isPresent() ? archived.get() : false),(companyName.isPresent()?companyName.get():null)),
                (new HashMap<String, List<String>>(){{
                    put("User",Arrays.asList("displayName"));
                }}),
                (new HashMap<String, List<String>>(){{
                    put("Job",Arrays.asList("jobDescription","jobScreeningQuestionsList","jobKeySkillsList","jobCapabilityList","jobHiringTeamList","jobDetail", "expertise", "education", "noticePeriod", "function", "experienceRange", "userEnteredKeySkill", "updatedOn", "updatedBy"));
                }})
        );
    }

    /**
     * Api to retrieve
     * 1. list candidates for job for specified stage
     * 2. count of candidates by each stage
     *
     * @param jobCandidateMapping The payload consisting of job id and stage
     *
     * @return response bean with all details as a json string
     * @throws Exception
     */
    @PostMapping(value = "/jobViewByStage")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    String getJobViewByIdAndStage(@RequestBody JobCandidateMapping jobCandidateMapping) throws Exception {
        SingleJobViewResponseBean responseBean = jobService.getJobViewById(jobCandidateMapping);

        return Util.stripExtraInfoFromResponseBean(responseBean,
                (new HashMap<String, List<String>>(){{
                    put("User",Arrays.asList("displayName"));
                }}),
                (new HashMap<String, List<String>>(){{
                    put("Job",Arrays.asList("jobDescription","jobScreeningQuestionsList","jobKeySkillsList","jobCapabilityList", "updatedOn", "updatedBy"));
                    put("Candidate", Arrays.asList("candidateEducationDetails","candidateProjectDetails",
                            "candidateOnlineProfiles","candidateWorkAuthorizations","candidateLanguageProficiencies","candidateSkillDetails","createdOn","createdBy", "firstName", "lastName", "displayName"));
                    put("JobCandidateMapping", Arrays.asList("updatedOn","updatedBy","techResponseData"));
                    put("CandidateDetails", Arrays.asList("id","candidateId"));
                    put("CandidateCompanyDetails", Arrays.asList("candidateId"));
                }})
        );
    }

    /**
     * Api to set the status of a job as published.
     *
     * @param jobId id of the job which is to be published
     * @throws Exception
     */
    @PutMapping(value = "/publishJob/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    void publishJob(@PathVariable("jobId") Long jobId) throws Exception {
        jobService.publishJob(jobId);
    }

    /**
     * Api to archive a job
     *
     * @param jobId id of the job to be archived
     * @throws Exception
     */
    @PutMapping(value = "/archiveJob/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    void archiveJob(@PathVariable("jobId") Long jobId) throws Exception {
        jobService.archiveJob(jobId);
    }

    /**
     * Api to unarchive a job
     *
     * @param jobId id of the job to be unarchived
     * @throws Exception
     */
    @PutMapping(value = "/unarchiveJob/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    void unarchiveJob(@PathVariable("jobId") Long jobId) throws Exception {
        jobService.unarchiveJob(jobId);
    }


    /**
     * Api for get job details based on job id
     *
     */
    @GetMapping(value = "/getDetails/{jobId}")
    @ResponseBody
    String getJobDetails(@PathVariable("jobId") Long jobId) throws Exception {
        return Util.stripExtraInfoFromResponseBean(
                jobService.getJobDetails(jobId),
                (new HashMap<String, List<String>>(){{
                    put("User",Arrays.asList("displayName"));
                }}),
                (new HashMap<String, List<String>>(){{
                    put("Job",new ArrayList<>(0));
                    put("JobScreeningQuestions",new ArrayList<>(0));
                    put("ScreeningQuestions",new ArrayList<>(0));
                    put("CompanyScreeningQuestion",new ArrayList<>(0));
                    put("UserScreeningQuestion",new ArrayList<>(0));
                }}));
        //return jobService.getJobDetails(jobId);
    }

    @GetMapping(value="/getHistory/{jobId}")
    @ResponseBody
    String getJobHistory(@PathVariable("jobId") Long jobId)throws Exception{
        return Util.stripExtraInfoFromResponseBean(
                jobService.getJobHistory(jobId),
                (new HashMap<String, List<String>>(){{
                    put("User",Arrays.asList("displayName"));
                }}),
                (new HashMap<String, List<String>>(){{
                    put("Job",new ArrayList<>(0));
                    put("JobScreeningQuestions",new ArrayList<>(0));
                    put("ScreeningQuestions",new ArrayList<>(0));
                    put("CompanyScreeningQuestion",new ArrayList<>(0));
                    put("UserScreeningQuestion",new ArrayList<>(0));
                }}));
    }
}