/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.Constant.IConstant;
import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.JobRepository;
import io.litmusblox.server.repository.JobScreeningQuestionsRepository;
import io.litmusblox.server.service.IJobService;
import io.litmusblox.server.service.JobResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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
public class JobService implements IJobService {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobScreeningQuestionsRepository jobScreeningQuestionsRepository;

    @Override
    public JobResponseBean addJob(Job job, String pageName) {//add job with respective pageName

        Job oldJob = null;
        if(null!=job.getId()){
             oldJob = jobRepository.findById(job.getId()).orElse(new Job());
        }

        if(pageName.equalsIgnoreCase(IConstant.OVERVIEW)){
            return addJobOverview(job,oldJob);
        }else if(pageName.equalsIgnoreCase(IConstant.SCREENING_QUESTIONS)){
            return addJobScreeningQuestions(job, oldJob);
        }
        return null;
    }

    private JobResponseBean addJobOverview(Job job, Job oldJob) { //method for add job for Overview page

        String title=job.getJobTitle();
        //validate title
       if(title.length()>IConstant.TITLE_MAX_LENGTH){  //Truncate job title if it is greater than max length
            title=title.substring(0,IConstant.TITLE_MAX_LENGTH);
        }

        if(null!=oldJob){//only update existing job
            //set job id from the db object
            job.setId(oldJob.getId());
            job.setUpdatedOn(new Date());
            jobRepository.save(job);
        }else{ //Create new entry for job
            job.setCreatedOn(new Date());
            job.setMlDataAvailable(false);
            //TODO: Remove the following piece of code and set the user & company as obtained from login
            User u = new User();
            u.setId(1L);
            job.setCreatedBy(u);
            Company c = new Company();
            c.setId(1L);
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
