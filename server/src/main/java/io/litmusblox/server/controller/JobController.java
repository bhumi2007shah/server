/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */
package io.litmusblox.server.controller;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobCandidateMapping;
import io.litmusblox.server.service.IJobService;
import io.litmusblox.server.service.JobResponseBean;
import io.litmusblox.server.service.SingleJobViewResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
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
@CrossOrigin
@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    IJobService jobService;

    @PostMapping(value = "/createJob/{pageName}")
    JobResponseBean addJob(@RequestBody Job job, @PathVariable ("pageName") String pageName) throws Exception {
        return jobService.addJob(job, pageName);
    }

    /**
     * Api for retrieving a list of jobs created by user
     * @param archived optional flag indicating if a list of archived jobs is requested. By default only open jobs will be returned
     * @return response bean with a list of jobs, count of open jobs and count of archived jobs
     * @throws Exception
     */
    @GetMapping(value = "/listOfJobs")
    String listAllJobsForUser(@RequestParam("archived") Optional<Boolean> archived) throws Exception {

        return stripExtraInfoFromResponseBean(jobService.findAllJobsForUser(archived.isPresent() ? archived.get() : false));
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
    @GetMapping(value = "/jobViewByStage")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    String getJobViewByIdAndStage(@RequestBody JobCandidateMapping jobCandidateMapping) throws Exception {
        SingleJobViewResponseBean responseBean = jobService.getJobViewById(jobCandidateMapping);

        return stripExtraInfoFromResponseBean(responseBean);
    }


    private String stripExtraInfoFromResponseBean(Object responseBean) {

        ObjectMapper mapper = new ObjectMapper();

        String json="";
        try {

            FilterProvider filters = new SimpleFilterProvider().addFilter("UserClassFilter", SimpleBeanPropertyFilter.filterOutAllExcept(new HashSet<String>(Arrays
                    .asList("displayName"))))
                    .addFilter("JobClassFilter", SimpleBeanPropertyFilter.serializeAllExcept(new HashSet<String>(Arrays.asList("jobScreeningQuestionsList","jobKeySkillsList","jobCapabilityList", "updatedOn", "updatedBy"))));
            json = mapper.writer(filters).writeValueAsString(responseBean);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return json;
    }
}