/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.Constant.IConstant;
import io.litmusblox.server.Constant.IErrorMessages;
import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.*;
import io.litmusblox.server.service.IJobService;
import io.litmusblox.server.service.JobResponseBean;
import io.litmusblox.server.service.JobWorspaceBean;
import io.litmusblox.server.service.JobWorspaceResponseBean;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.*;

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

    @Autowired
    TempSkillsRepository tempSkillsRepository;

    @Autowired
    JobKeySkillsRepository jobKeySkillsRepository;

    @Autowired
    JobCapabilitiesRepository jobCapabilitiesRepository;

    @Autowired
    SkillMasterRepository skillMasterRepository;

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
        }else if(pageName.equalsIgnoreCase(IConstant.SKILLS)){
            responseBean = addJobKeySkills(job, oldJob);
        }else if(pageName.equalsIgnoreCase(IConstant.CAPABILITIES)){
            responseBean = addJobCapabilities(job,oldJob);
        } else {
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

    @Transactional
    private JobResponseBean addJobKeySkills(Job job, Job oldJob){ //update and add new key skill
        if(null!=job.getJobKeySkillsList() && job.getJobKeySkillsList().isEmpty()){
            throw new ValidationException("Job key skills "+ IErrorMessages.EMPTY_AND_NULL_MESSAGE + oldJob.getId());
        }
        Map<String,Long> skillsMasterMap=new HashMap<>();
        Map<String,Long> tempSkillsMap=new HashMap<>();

        // update ML_PROVIDED value from true to false
        jobKeySkillsRepository.updateJobKeySkills(false, true,job.getId());

        List<JobKeySkills> falseJobKeySkillslist = jobKeySkillsRepository.findByJobIdAndMlProvided(job.getId(), false);

        if(falseJobKeySkillslist.size()>0){
            //delete all key skills where MlProvided=false
            jobKeySkillsRepository.deleteAll(falseJobKeySkillslist);
        }
        skillMasterRepository.findAll().forEach(skillsMaster -> skillsMasterMap.put(skillsMaster.getSkillsName(),skillsMaster.getId()));
        tempSkillsRepository.findAll().forEach(tempSkill-> tempSkillsMap.put(tempSkill.getSkillName(),tempSkill.getId()));

        User u = userRepository.getOne(1L);
        for (String userSkills:job.getUserEnteredKeySkill()) {

            if(skillsMasterMap.keySet().contains(userSkills)){
                SkillsMaster skillsMaster=skillMasterRepository.getOne(skillsMasterMap.get(userSkills));
                JobKeySkills jobKeySkills=jobKeySkillsRepository.findByJobIdAndSkillId(job.getId(), skillsMaster);
                if(null!=jobKeySkills){
                    continue;
                }else{
                    setJobKeySkills(u,null, skillsMaster,job);
                }

            }else if(tempSkillsMap.keySet().contains(userSkills)){
                TempSkills tempSkills=tempSkillsRepository.getOne(tempSkillsMap.get(userSkills));
                setJobKeySkills(u,tempSkills, null,job);
            }else{
                TempSkills tempSkills=new TempSkills();
                tempSkills.setReviewed(false);
                tempSkills.setSkillName(userSkills);
                tempSkills=tempSkillsRepository.save(tempSkills);
                setJobKeySkills(u,tempSkills, null,job);
            }

        }

        job.getJobKeySkillsList().forEach(jobSkill->{jobKeySkillsRepository.updateJobKeySkillById(true,jobSkill.getId());/*jobSkill.setCreatedBy(u);jobSkill.setCreatedOn(new Date());jobSkill.setUpdatedOn(new Date());jobSkill.setUpdatedBy(u);jobSkill.setJobId(job.getId());*/});
       /* oldJob.getJobKeySkillsList().addAll(job.getJobKeySkillsList());
        jobRepository.save(oldJob);*/
        JobResponseBean jb=new JobResponseBean();
        jb.setJobId(job.getId());
        return jb;
    }

    private void setJobKeySkills(User u,TempSkills temp,SkillsMaster skill,Job job){
        JobKeySkills jobKeySkills=new JobKeySkills();
        jobKeySkills.setMlProvided(false);
        jobKeySkills.setSelected(true);
        jobKeySkills.setCreatedOn(new Date());
        jobKeySkills.setCreatedBy(u);
        if(null!=skill){
            jobKeySkills.setSkillId(skill);
        }else if(null!=temp){
            jobKeySkills.setSkillIdFromTemp(temp);
        }
        jobKeySkills.setJobId(job.getId());
        jobKeySkillsRepository.save(jobKeySkills);
    }

    private JobResponseBean addJobCapabilities(Job job,Job oldJob){ //add job capabilities

        if(null!=job.getJobCapabilityList() && job.getJobCapabilityList().isEmpty()){
            throw new ValidationException("Job Capabilities "+ IErrorMessages.EMPTY_AND_NULL_MESSAGE + job.getId());
        }
        List<Long> capabilityList = new ArrayList<>();
        job.getJobCapabilityList().forEach(jobCapabilities->capabilityList.add(jobCapabilities.getId()));

        //update all capability list as unselected
        jobCapabilitiesRepository.updateJobCapabilitiesForUnSelected(false, job.getId());

        //update all capability list as selected
        jobCapabilitiesRepository.updateJobCapabilitiesForSelected(true,job.getId(),capabilityList);
        oldJob.setStatus(IConstant.PUBLISHED);
        jobRepository.save(oldJob);
        JobResponseBean jb=new JobResponseBean();
        jb.setJobId(job.getId());
        return jb;
    }
}
