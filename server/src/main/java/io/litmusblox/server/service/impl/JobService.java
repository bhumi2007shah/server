/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.Constant.IConstant;
import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.JobCandidateMapping;
import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.*;
import io.litmusblox.server.service.IJobService;
import io.litmusblox.server.service.JobResponseBean;
import io.litmusblox.server.service.JobWorspaceResponseBean;
import io.litmusblox.server.service.SingleJobViewResponseBean;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Implementation class for JobService
 *
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 9:47 AM
 * Class Name : JobService
 * Project Name : server
 */
@Service
@Log4j2
public class JobService implements IJobService {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JobScreeningQuestionsRepository jobScreeningQuestionsRepository;

    @Resource
    JobCandidateMappingRepository jobCandidateMappingRepository;

    @Override
    public JobResponseBean addJob(Job job, String pageName) throws Exception {//add job with respective pageName

        log.info("Received request to add job for page "+pageName);
        long startTime = System.currentTimeMillis();

        Job oldJob = null;
        if(null!=job.getId()){
             oldJob = jobRepository.findById(job.getId()).orElse(new Job());
        }

        JobResponseBean responseBean = null;

        if(pageName.equalsIgnoreCase(IConstant.OVERVIEW)){
            responseBean = addJobOverview(job,oldJob);
        }else if(pageName.equalsIgnoreCase(IConstant.SCREENING_QUESTIONS)){
            responseBean = addJobScreeningQuestions(job, oldJob);
        }else {
            //throw an operation not supported exception
        }

        log.info("Completed processing request to add job in " + (System.currentTimeMillis() - startTime) + "ms");
        return responseBean;
    }

    /**
     * Fetch details of currently logged in user and
     * query the repository to find the list of all jobs
     *
     * @param archived flag indicating if only archived jobs need to be fetched
     * @return List of jobs created by the logged in user
     */
    @Transactional
    public JobWorspaceResponseBean findAllJobsForUser(boolean archived) throws Exception {
        //TODO: replace user id code below with values from logged in user
        Long userId = 2L;
        //end of code to be replaced
        User loggedInUser = userRepository.getOne(userId);
        JobWorspaceResponseBean responseBean = new JobWorspaceResponseBean();
        if(archived) {
            responseBean.setListOfJobs(jobRepository.findByCreatedByAndDateArchivedIsNotNull(loggedInUser));
            responseBean.setArchivedJobs(responseBean.getListOfJobs().size());
            responseBean.setOpenJobs((jobRepository.countByCreatedByAndDateArchivedIsNull(loggedInUser)).intValue());
        }
        else {
            responseBean.setListOfJobs(jobRepository.findByCreatedByAndDateArchivedIsNull(loggedInUser));
            responseBean.setOpenJobs(responseBean.getListOfJobs().size());
            responseBean.setArchivedJobs((jobRepository.countByCreatedByAndDateArchivedIsNotNull(loggedInUser)).intValue());
        }

        return responseBean;
    }

    /**
     * For the specified job, retrieve
     * 1. list candidates for job for specified stage
     * 2. count of candidates by each stage
     *
     * @return response bean with all details
     * @throws Exception
     */
    @Transactional
    public SingleJobViewResponseBean getJobViewById(JobCandidateMapping jobCandidateMapping) throws Exception {

        SingleJobViewResponseBean responseBean = new SingleJobViewResponseBean();
       responseBean.setCandidateList(jobCandidateMappingRepository.findByJobIdAndStage(jobCandidateMapping.getJobId(), jobCandidateMapping.getStage()));

        List<Object[]> stageCountList = jobCandidateMappingRepository.findCandidateCountByStage(jobCandidateMapping.getJobId().getId());

        stageCountList.stream().forEach(objArray -> {
            responseBean.getCandidateCountByStage().put(((Integer)objArray[0]).longValue(),((BigInteger)objArray[1]).intValue());
        });

        return responseBean;
    }

       private JobResponseBean addJobOverview(Job job, Job oldJob) { //method for add job for Overview page

       //validate title
       if(job.getJobTitle().length()>IConstant.TITLE_MAX_LENGTH)  //Truncate job title if it is greater than max length
            job.setJobTitle(job.getJobTitle().substring(0,IConstant.TITLE_MAX_LENGTH));


        if(null!=oldJob){//only update existing job
            //set job id from the db object
            job.setId(oldJob.getId());
            job.setUpdatedOn(new Date());
            jobRepository.save(job);
        }else{ //Create new entry for job
            job.setCreatedOn(new Date());
            job.setMlDataAvailable(false);
            //TODO: Remove the following piece of code and set the user & company as obtained from login
            User u = userRepository.getOne(2L);
            job.setCreatedBy(u);
            Company c = companyRepository.getOne(1L);
            job.setCompanyId(c);
            //End of code to be removed
            jobRepository.save(job);
        }
        JobResponseBean jb=new JobResponseBean();
        jb.setJobId(job.getId());
        return jb;
        //return new JobResponseBean(job.getId());
    }

    private JobResponseBean addJobScreeningQuestions(Job job, Job oldJob){ //method for add screening questions

        if(null!=oldJob.getJobScreeningQuestionsList()){
            jobScreeningQuestionsRepository.deleteAll(oldJob.getJobScreeningQuestionsList());//delete old job screening question list
        }
        oldJob.getJobScreeningQuestionsList().addAll(job.getJobScreeningQuestionsList());//add new screening question list
        jobRepository.save(oldJob);
        JobResponseBean jb=new JobResponseBean();
        jb.setJobId(job.getId());
        return jb;
    }
}
