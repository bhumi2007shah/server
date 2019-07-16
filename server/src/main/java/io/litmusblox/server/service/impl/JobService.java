/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.*;
import io.litmusblox.server.service.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.naming.OperationNotSupportedException;
import javax.validation.ValidationException;
import java.math.BigInteger;
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

    @Resource
    JobRepository jobRepository;

    @Resource
    CompanyRepository companyRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    JobScreeningQuestionsRepository jobScreeningQuestionsRepository;

    @Resource
    JobCandidateMappingRepository jobCandidateMappingRepository;

    @Resource
    TempSkillsRepository tempSkillsRepository;

    @Resource
    JobKeySkillsRepository jobKeySkillsRepository;

    @Resource
    JobCapabilitiesRepository jobCapabilitiesRepository;

    @Resource
    SkillMasterRepository skillMasterRepository;

    @Resource
    CompanyAddressRepository companyAddressRepository;

    @Resource
    CompanyBuRepository companyBuRepository;

    @Autowired
    IScreeningQuestionService screeningQuestionService;


    @Transactional(propagation = Propagation.REQUIRED)
    public Job addJob(Job job, String pageName) throws Exception {//add job with respective pageName

        log.info("Received request to add job for page "+pageName);
        long startTime = System.currentTimeMillis();

        Job oldJob = null;

        if (null != job.getId()) {
            //get handle to existing job object
            Optional<Job> tempJobObj = jobRepository.findById(job.getId());
            oldJob = tempJobObj.isPresent() ? tempJobObj.get() : null;
        }

        switch(IConstant.AddJobPages.valueOf(pageName)) {
            case overview:
                addJobOverview(job,oldJob);
                break;
            case screeningQuestions:
                addJobScreeningQuestions(job, oldJob);
                break;
            case keySkills:
                addJobKeySkills(job,oldJob);
                break;
            case capabilities:
                addJobCapabilities(job, oldJob);
                break;
            case jobDetail:
                addJobDetail(job, oldJob);
                break;
            default:
                throw new OperationNotSupportedException("Unknown page: " + pageName);
        }

        log.info("Completed processing request to add job in " + (System.currentTimeMillis() - startTime) + "ms");
        return job;
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

        log.info("Received request to request to find all jobs for user for archived = " + archived);
        long startTime = System.currentTimeMillis();

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
        log.info("Completed processing request to find all jobs for user in " + (System.currentTimeMillis() - startTime) + "ms");

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
        log.info("Received request to request to find a list of all candidates for job: " + jobCandidateMapping.getJobId().getId() + " and stage: " + jobCandidateMapping.getStage().getId());
        long startTime = System.currentTimeMillis();

        SingleJobViewResponseBean responseBean = new SingleJobViewResponseBean();
       responseBean.setCandidateList(jobCandidateMappingRepository.findByJobIdAndStage(jobCandidateMapping.getJobId(), jobCandidateMapping.getStage()));

        List<Object[]> stageCountList = jobCandidateMappingRepository.findCandidateCountByStage(jobCandidateMapping.getJobId().getId());

        stageCountList.stream().forEach(objArray -> {
            responseBean.getCandidateCountByStage().put(((Integer)objArray[0]).longValue(),((BigInteger)objArray[1]).intValue());
        });
        log.info("Completed processing request to find candidates for job " + jobCandidateMapping.getJobId().getId() + " and stage: " + jobCandidateMapping.getStage().getId() + (System.currentTimeMillis() - startTime) + "ms");

        return responseBean;
    }

    private void addJobOverview(Job job, Job oldJob) { //method for add job for Overview page

       //validate title
       if(job.getJobTitle().length()>IConstant.TITLE_MAX_LENGTH)  //Truncate job title if it is greater than max length
            job.setJobTitle(job.getJobTitle().substring(0,IConstant.TITLE_MAX_LENGTH));


        if(null!=oldJob){//only update existing job
            //set job id from the db object
            job.setId(oldJob.getId());
            job.setCreatedBy(oldJob.getCreatedBy());
            job.setCreatedOn(oldJob.getCreatedOn());
            job.setStatus(oldJob.getStatus());
            job.setMlDataAvailable(false);
            job.setUpdatedOn(new Date());

            //remove all data from job_key_skills and job_capabilities
            //TODO: Uncomment following
            //jobKeySkillsRepository.deleteByJobId(job.getId());
            jobCapabilitiesRepository.deleteByJobId(job.getId());

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

        //TODO: Add call to ml api? scheduled task?
    }

    private void addJobScreeningQuestions(Job job, Job oldJob) throws Exception { //method for add screening questions

        if(job.getJobScreeningQuestionsList().size()>IConstant.SCREENING_QUESTIONS_LIST_MAX_SIZE){
            throw new ValidationException(IErrorMessages.SCREENING_QUESTIONS_VALIDATION_MESSAGE+job.getId());
        }

        if(null!=oldJob.getJobScreeningQuestionsList() && oldJob.getJobScreeningQuestionsList().size()>0){
            jobScreeningQuestionsRepository.deleteAll(oldJob.getJobScreeningQuestionsList());//delete old job screening question list
        }
        //TODO:User is need to change
        User u = userRepository.getOne(2L);

        job.getJobScreeningQuestionsList().forEach(n->{n.setCreatedBy(u);n.setCreatedOn(new Date());n.setJobId(job.getId());});
        jobScreeningQuestionsRepository.saveAll(job.getJobScreeningQuestionsList());

        //populate capabilities and key skills for the job
        job.setJobKeySkillsList(jobKeySkillsRepository.findByJobId(job.getId()));
    }

    private void addJobKeySkills(Job job, Job oldJob) throws Exception { //update and add new key skill
        if(null!=job.getJobKeySkillsList() && job.getJobKeySkillsList().isEmpty()){
            throw new ValidationException("Job key skills "+ IErrorMessages.EMPTY_AND_NULL_MESSAGE + oldJob.getId());
        }

        //TODO: replace this code to use the logged in user
        User u = userRepository.getOne(2L);

        //delete all key skills where MlProvided=false
        List<JobKeySkills> userProvidedJobKeySkillslist = jobKeySkillsRepository.findByJobIdAndMlProvided(job.getId(), false);
        if(userProvidedJobKeySkillslist.size()>0){
            jobKeySkillsRepository.deleteAll(userProvidedJobKeySkillslist);
        }

        //For each keyskill in the request (will have only the mlProvided true ones), update the values for selected
        Map<Long, JobKeySkills> newSkillValues = new HashMap();
        job.getJobKeySkillsList().stream().forEach(jobKeySkill -> newSkillValues.put(jobKeySkill.getSkillId().getId(), jobKeySkill));

        oldJob.getJobKeySkillsList().forEach(oldKeySkill -> {
            if(oldKeySkill.getMlProvided()) {
                JobKeySkills newValue = newSkillValues.get(oldKeySkill.getSkillId().getId());
                oldKeySkill.setSelected(newValue.getSelected());
                oldKeySkill.setUpdatedOn(new Date());
                oldKeySkill.setUpdatedBy(u);
            }
        });

        //get all skillMaster and tempskills master data
        Map<String,Long> skillsMasterMapByName=new HashMap<>();
        Map<Long, SkillsMaster> skillsMasterMap = new HashMap<>();
        List<SkillsMaster> skillsMasterList = skillMasterRepository.findAll();
        skillsMasterList.forEach( skillsMaster -> {
            skillsMasterMapByName.put(skillsMaster.getSkillName(),skillsMaster.getId());
            skillsMasterMap.put(skillsMaster.getId(), skillsMaster);
        });

        Map<String,Long> tempSkillsMapByName=new HashMap<>();
        Map<Long, TempSkills> tempSkillsMap = new HashMap<>();
        tempSkillsRepository.findAll().forEach(tempSkill-> {
            tempSkillsMapByName.put(tempSkill.getSkillName(),tempSkill.getId());
            tempSkillsMap.put(tempSkill.getId(), tempSkill);
        });

        //For each user entered key skill, do the following:
        //(i) check if the skill is already present in the ml key skill list for the job. If yes, skip this skill
        //(ii) if not, check if the skill is present in the skills master table. If yes, use that skill id to insert a record in the db
        //(iii) if not, check if the skill is present in the temp key skills table. If yes, use that id and insert a record in the db with temp key skill id column populated
        //(iv) if not, enter a record in the temp key skills table and use the id of the newly inserted record to insert a record in job key skill table with the temp key skill id column populated

        for (String userSkills:job.getUserEnteredKeySkill()) {

            if(skillsMasterMapByName.keySet().contains(userSkills)){
                Long skillId = skillsMasterMapByName.get(userSkills);
                //does the skill match one of the those provided by ML?
                if(null != newSkillValues.get(skillId)) {
                    //found a match, skip this skill
                    continue;
                }
                else {
                    //no match found in mlProvided skill, add a record
                    jobKeySkillsRepository.save(new JobKeySkills(skillsMasterMap.get(skillId),false,true,new Date(),u, job.getId()));
                }

            }
            //check if the user entered skill exists in the temp skills table
            else if(tempSkillsMapByName.keySet().contains(userSkills)){
                Long tempSkillId = tempSkillsMapByName.get(userSkills);
                jobKeySkillsRepository.save(new JobKeySkills(tempSkillsMap.get(tempSkillId), false, true, new Date(), u, job.getId()));

            }
            //this is a new skill, add to temp skills and refer to jobkeyskills table
            else{
                TempSkills tempSkills = tempSkillsRepository.save(new TempSkills(userSkills, false));
                jobKeySkillsRepository.save(new JobKeySkills(tempSkills, false, true, new Date(), u, job.getId()));
            }
        }
        job.setJobCapabilityList(jobCapabilitiesRepository.findByJobId(job.getId()));
    }


    private void addJobCapabilities(Job job,Job oldJob){ //add job capabilities

        if(null!=job.getJobCapabilityList() && job.getJobCapabilityList().isEmpty()){
            throw new ValidationException("Job Capabilities "+ IErrorMessages.EMPTY_AND_NULL_MESSAGE + job.getId());
        }

        //For each capability in the request, update the values for selected and importance_level
        Map<Long, JobCapabilities> newCapabilityValues = new HashMap();
        job.getJobCapabilityList().stream().forEach(jobCapability -> newCapabilityValues.put(jobCapability.getId(), jobCapability));

        oldJob.getJobCapabilityList().forEach(oldCapability -> {
            JobCapabilities newValue = newCapabilityValues.get(oldCapability.getId());
            oldCapability.setImportanceLevel(newValue.getImportanceLevel());
            oldCapability.setSelected(newValue.getSelected());
            oldCapability.setUpdatedOn(new Date());
            oldCapability.setUpdatedBy(userRepository.getOne(2L)); //TODO: replace this by getting the logged in user
        });


        oldJob.setStatus(IConstant.JobStatus.PUBLISHED.getValue());
        oldJob.setDatePublished(new Date());
        jobRepository.save(oldJob);

        job.getJobCapabilityList().clear();
        job.getJobCapabilityList().addAll(oldJob.getJobCapabilityList());
    }

    private void addJobDetail(Job job,Job oldJob){//add job details

        if(null==job.getJobDetail()){
            throw new ValidationException("Job detail "+IErrorMessages.NULL_MESSAGE +job.getId());
        }

        MasterDataBean masterDataBean=MasterDataBean.getInstance();
        if(null==masterDataBean.getFunction().get(job.getJobDetail().getFunction().getId())){
            throw new ValidationException("In Job detail, function "+IErrorMessages.NULL_MESSAGE +job.getId());
        }

        if(null==masterDataBean.getEducation().get(job.getJobDetail().getEducation().getId())){
            throw new ValidationException("In Job detail, education "+IErrorMessages.NULL_MESSAGE +job.getId());
        }

        if(null==masterDataBean.getExpertise().get(job.getJobDetail().getExpertise().getId())){
            throw new ValidationException("In Job detail, expertise "+IErrorMessages.NULL_MESSAGE +job.getId());
        }

        //TODO:replace companyId by getting the logged in user
        List<CompanyAddress> companyAddressList = companyAddressRepository.findByCompanyId(1l);
        List<CompanyBu> companyBuList = companyBuRepository.findByCompanyId(1l);

        Map<Long, CompanyBu> companyBuMap = new HashMap<>();
        Map<Long, CompanyAddress> companyAddressMap = new HashMap<>();

        companyBuList.forEach(companyBu -> companyBuMap.put(companyBu.getId(), companyBu));
        companyAddressList.forEach(companyAddress -> companyAddressMap.put(companyAddress.getId(), companyAddress));

        if(companyAddressList.isEmpty() || null==companyAddressMap.get(job.getJobDetail().getJobLocation().getId())
                || null==companyAddressMap.get(job.getJobDetail().getInterviewLocation().getId())){
            throw new ValidationException("In Job detail, company address "+IErrorMessages.NULL_MESSAGE +job.getId());
        }

        if(companyBuList.isEmpty() || null==companyBuMap.get(job.getJobDetail().getBuId().getId())){
            throw new ValidationException("In Job detail, company bu "+IErrorMessages.NULL_MESSAGE +job.getId());
        }

        String expRange = masterDataBean.getExperienceRange().get(job.getJobDetail().getExperienceRange().getId());

        if(null == expRange){
            throw new ValidationException("In Job detail, experience Range "+IErrorMessages.NULL_MESSAGE +job.getId());
        }

        List<User> userList = userRepository.findByCompanyId(1l);

        JobDetail detail=job.getJobDetail();
        detail.getUserList().addAll(userList);
        String[] range = masterDataBean.getExperienceRange().get(job.getJobDetail().getExperienceRange().getId()).split(" ");
        detail.setMinExperience(Double.parseDouble(range[0]));
        detail.setMaxExperience(Double.parseDouble(range[2]));
        detail.setJobId(oldJob.getId());
        oldJob.setJobDetail(detail);
        /*oldJob.getJobDetail().setBuId(job.getJobDetail().getBuId());
        oldJob.getJobDetail().setEducation(detail.getEducation());
        oldJob.getJobDetail().setExpertise(detail.getExpertise());
        oldJob.getJobDetail().setFunction(detail.getFunction());
        oldJob.getJobDetail().setInterviewLocation(detail.getInterviewLocation());
        oldJob.getJobDetail().setJobLocation(detail.getJobLocation());
        oldJob.getJobDetail().setMaxSalary(detail.getMaxSalary());
        oldJob.getJobDetail().setMinSalary(detail.getMinSalary());*/

        oldJob.setUpdatedOn(new Date());

        jobRepository.save(oldJob);

    }
}
