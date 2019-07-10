/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.Constant.IConstant;
import io.litmusblox.server.model.*;
import io.litmusblox.server.Constant.IErrorMessages;
import io.litmusblox.server.repository.*;
import io.litmusblox.server.service.IJobService;
import io.litmusblox.server.service.JobResponseBean;
import io.litmusblox.server.service.JobWorspaceBean;
import io.litmusblox.server.service.JobWorspaceResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.ValidationException;
import java.util.ArrayList;
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
public class JobService implements IJobService {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JobScreeningQuestionsRepository jobScreeningQuestionsRepository;

    @Autowired
    TempSkillsRepository tempSkillsRepository;

    @Autowired
    JobKeySkillsRepository jobKeySkillsRepository;

    @Autowired
    JobCapabilitiesRepository jobCapabilitiesRepository;

    @Override
    public JobResponseBean addJob(Job job, String pageName) throws Exception {//add job with respective pageName

        Job oldJob = null;
        if(null!=job.getId()){
             oldJob = jobRepository.findById(job.getId()).orElse(new Job());
        }

        if(pageName.equalsIgnoreCase(IConstant.OVERVIEW)){
            return addJobOverview(job,oldJob);
        }else if(pageName.equalsIgnoreCase(IConstant.SCREENING_QUESTIONS)){
            return addJobScreeningQuestions(job, oldJob);
        }else if(pageName.equalsIgnoreCase(IConstant.SKILLS)){
            return addJobKeySkills(job, oldJob);
        } else {
            //throw an operation not supported exception
        }

        return null;
    }

    /**
     * Fetch details of currently logged in user and
     * query the repository to find the list of all jobs
     *
     * @param archived flag indicating if only archived jobs need to be fetched
     * @return List of jobs created by the logged in user
     */
    @Override
    public JobWorspaceResponseBean findAllJobsForUser(boolean archived) throws Exception {
        //TODO: replace user id code below with values from logged in user
        Long userId = 2L;
        //end of code to be replaced
        User loggedInUser = userRepository.getOne(userId);
        JobWorspaceResponseBean responseBean = new JobWorspaceResponseBean();
        if(archived) {
            responseBean.setListOfJobs(convertToResponseBeans(jobRepository.findByCreatedByAndDateArchivedIsNotNull(loggedInUser)));
            responseBean.setArchivedJobs(responseBean.getListOfJobs().size());
            responseBean.setOpenJobs((jobRepository.countByCreatedByAndDateArchivedIsNull(loggedInUser)).intValue());
        }
        else {
            responseBean.setListOfJobs(convertToResponseBeans(jobRepository.findByCreatedByAndDateArchivedIsNull(loggedInUser)));
            responseBean.setOpenJobs(responseBean.getListOfJobs().size());
            responseBean.setArchivedJobs((jobRepository.countByCreatedByAndDateArchivedIsNotNull(loggedInUser)).intValue());
        }

        return responseBean;
    }

    private List<JobWorspaceBean> convertToResponseBeans(List<Job> allJobs) {
        List<JobWorspaceBean> responseBeanList = new ArrayList<>(allJobs.size());
        allJobs.stream().forEach(job -> {
            JobWorspaceBean responseBean = new JobWorspaceBean(job.getId(), job.getStatus(),
                    job.getJobTitle(), job.getCompanyJobId(),
                    job.getNoOfPositions(), job.getDatePublished(), job.getCreatedBy().getDisplayName());
            if (null != job.getJobDetail()) {
                responseBean.setJobLocation(job.getJobDetail().getJobLocation().getAddress());
                responseBean.setBusinessUnit(job.getJobDetail().getBuId().getBusinessUnit());
                responseBean.setFunction(job.getJobDetail().getFunction().getValue());
            }
            responseBeanList.add(responseBean);
        });
        return responseBeanList;
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
            User u = userRepository.getOne(1L);
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

        if(job.getJobScreeningQuestionsList().size()>IConstant.SCREENING_QUESTIONS_LIST_MAX_SIZE){
            throw new ValidationException(IErrorMessages.SCREENING_QUESTIONS_VALIDATION_MESSAGE+job.getId());
        }
        if(null!=oldJob.getJobScreeningQuestionsList() && oldJob.getJobScreeningQuestionsList().size()>0){
            jobScreeningQuestionsRepository.deleteAll(oldJob.getJobScreeningQuestionsList());//delete old job screening question list
        }
        //TODO:User is need to change
        User u = userRepository.getOne(1L);

        job.getJobScreeningQuestionsList().forEach(n->{n.setCreatedBy(u);n.setCreatedOn(new Date());n.setUpdatedOn(new Date());n.setUpdatedBy(u);});
        jobScreeningQuestionsRepository.saveAll(job.getJobScreeningQuestionsList());
        JobResponseBean jb=new JobResponseBean();
        jb.setJobId(job.getId());
        return jb;
    }

    private JobResponseBean addJobKeySkills(Job job, Job oldJob){ //update and add new key skill
        if(null!=oldJob.getJobKeySkillsList() && oldJob.getJobKeySkillsList().size()>0){
            throw new ValidationException("Job key skills "+ IErrorMessages.EMPTY_AND_NULL_MESSAGE + oldJob.getId());
        }
        List<TempSkills> tempSkillsList = new ArrayList<>();

        // update ML_PROVIDED value from true to false
        jobKeySkillsRepository.updateJobKeySkills(false, true,job.getId());

        List<JobKeySkills> falseJobKeySkillslist = jobKeySkillsRepository.findByJobIdAndMlProvided(job.getId(), false);

        //delete all key skills where MlProvided=false
        jobKeySkillsRepository.deleteAll(falseJobKeySkillslist);

        for (JobKeySkills jobKeySkills: job.getJobKeySkillsList()) {
            if(null!=jobKeySkills.getId()){
                jobKeySkills.setSelcted(true);
            }else{
                List<TempSkills> tempList = tempSkillsRepository.findAll();
                if(tempList.contains(jobKeySkills.getSkillId().getSkillsMaster())){
                    continue;
                }
                TempSkills tempSkills=new TempSkills();
                tempSkills.setReviewed(false);
                tempSkills.setSkillName(jobKeySkills.getSkillId().getSkillsMaster());
                tempSkills = tempSkillsRepository.save(tempSkills);
                tempSkillsList.add(tempSkills);
            }
        }

        for (TempSkills tempSkills:tempSkillsList) {
            JobKeySkills jobKeySkills=new JobKeySkills();
            jobKeySkills.setMlProvided(false);
            jobKeySkills.setSelcted(true);
            jobKeySkills.setCreatedOn(new Date());
            jobKeySkills.setSkillIdFromTemp(tempSkills);
        }

        oldJob.getJobKeySkillsList().addAll(job.getJobKeySkillsList());
        jobRepository.save(oldJob);
        JobResponseBean jb=new JobResponseBean();
        jb.setJobId(job.getId());
        return jb;
    }

    private JobResponseBean addJobCapabilities(Job job){ //add job capabilities

        if(null!=job.getJobCapabilityList() && job.getJobCapabilityList().size()>0){
            throw new ValidationException("Job Capabilities "+ IErrorMessages.EMPTY_AND_NULL_MESSAGE + job.getId());
        }
        List<Long> capabilityList = new ArrayList<>();
        job.getJobCapabilityList().forEach(n->capabilityList.add(n.getId()));

        //update all capability list as unselected
        jobCapabilitiesRepository.updateJobCapabilitiesForUnSelected(false, job.getId());

        //update all capability list as selected
        jobCapabilitiesRepository.updateJobCapabilitiesForSelected(true,job.getId(),capabilityList);

        JobResponseBean jb=new JobResponseBean();
        jb.setJobId(job.getId());
        return jb;
    }


}
