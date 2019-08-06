/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */
package io.litmusblox.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobCandidateMapping;
import io.litmusblox.server.service.IJobService;
import io.litmusblox.server.service.SingleJobViewResponseBean;
import io.litmusblox.server.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
        Job job = new ObjectMapper().readValue(jobStr, Job.class);

        return Util.stripExtraInfoFromResponseBean(
            jobService.addJob(job, pageName),
            (new HashMap<String, List<String>>(){{
                put("User",Arrays.asList("displayName","id"));
                put("ScreeningQuestion", Arrays.asList("question"));
            }}),
            (new HashMap<String, List<String>>(){{
                put("Job",Arrays.asList("createdOn","createdBy", "updatedOn", "updatedBy"));
                put("CompanyScreeningQuestion", Arrays.asList("createdOn", "createdBy", "updatedOn", "updatedBy","company"));
                put("UserScreeningQuestion", Arrays.asList("createdOn", "updatedOn","userId"));
                put("JobScreeningQuestions", Arrays.asList("id","jobId","createdBy", "createdOn", "updatedOn","updatedBy"));
            }})
        );

       // return jobService.addJob(job, pageName);
    }

    /**
     * Api for retrieving a list of jobs created by user
     * @param archived optional flag indicating if a list of archived jobs is requested. By default only open jobs will be returned
     * @return response bean with a list of jobs, count of open jobs and count of archived jobs
     * @throws Exception
     */
    @GetMapping(value = "/listOfJobs")
    String listAllJobsForUser(@RequestParam("archived") Optional<Boolean> archived) throws Exception {

        return Util.stripExtraInfoFromResponseBean(
                jobService.findAllJobsForUser(archived.isPresent() ? archived.get() : false),
                (new HashMap<String, List<String>>(){{
                    put("User",Arrays.asList("displayName"));
                }}),
                (new HashMap<String, List<String>>(){{
                    put("Job",Arrays.asList("jobDescription","jobScreeningQuestionsList","jobKeySkillsList","jobCapabilityList","jobHiringTeamList","jobDetail", "updatedOn", "updatedBy","createdBy"));
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
                    put("Candidate", Arrays.asList("candidateDetails","candidateEducationDetails","candidateProjectDetails","candidateCompanyDetails",
                            "candidateOnlineProfiles","candidateWorkAuthorizations","candidateLanguageProficiencies","candidateSkillDetails","createdOn","createdBy"));
                    put("JobCandidateMapping", Arrays.asList("updatedOn","updatedBy"));
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

}