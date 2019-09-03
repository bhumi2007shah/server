/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.error.WebException;
import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.*;
import io.litmusblox.server.service.*;
import io.litmusblox.server.utils.RestClient;
import io.litmusblox.server.utils.SentryUtil;
import io.litmusblox.server.utils.Util;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.naming.OperationNotSupportedException;
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
    MasterDataRepository masterDataRepository;

    @Resource
    CompanyAddressRepository companyAddressRepository;

    @Resource
    CompanyBuRepository companyBuRepository;

    @Resource
    JobDetailRepository jobDetailRepository;

    @Resource
    CompanyStageStepRepository companyStageStepRepository;

    @Resource
    JobHiringTeamRepository jobHiringTeamRepository;

    @Resource
    JcmCommunicationDetailsRepository jcmCommunicationDetailsRepository;

    @Autowired
    IScreeningQuestionService screeningQuestionService;

    @Value("${mlApiUrl}")
    private String mlUrl;

    private static MasterData mediumImportanceLevel = null;

    @Transactional
    public Job addJob(Job job, String pageName) throws Exception {//add job with respective pageName

        log.info("Received request to add job for page " + pageName);
        long startTime = System.currentTimeMillis();

        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Job oldJob = null;

        if (null != job.getId()) {
            //get handle to existing job object
            Optional<Job> tempJobObj = jobRepository.findById(job.getId());
            oldJob = tempJobObj.isPresent() ? tempJobObj.get() : null;
        }

        switch (IConstant.AddJobPages.valueOf(pageName)) {
            case overview:
                addJobOverview(job, oldJob, loggedInUser);
                break;
            case screeningQuestions:
                addJobScreeningQuestions(job, oldJob, loggedInUser);
                break;
            case keySkills:
                addJobKeySkills(job, oldJob, loggedInUser);
                break;
            case capabilities:
                addJobCapabilities(job, oldJob, loggedInUser);
                break;
            case jobDetail:
                addJobDetail(job, oldJob, loggedInUser);
                break;
            case hiringTeam:
                addJobHiringTeam(job, oldJob, loggedInUser);
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
     * @param companyName name of the company for which jobs have to be found
     * @return List of jobs created by the logged in user
     */
    @Transactional
    public JobWorspaceResponseBean findAllJobsForUser(boolean archived, String companyName) throws Exception {

        log.info("Received request to request to find all jobs for user for archived = " + archived);
        long startTime = System.currentTimeMillis();

        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        JobWorspaceResponseBean responseBean = new JobWorspaceResponseBean();

        switch(loggedInUser.getRole()) {
            case IConstant.UserRole.Names.CLIENT_ADMIN:
                log.info("Request from Client Admin, all jobs for the company will be returned");
                jobsForCompany(responseBean, archived, loggedInUser.getCompany());
                break;
            case IConstant.UserRole.Names.SUPER_ADMIN:
                if (Util.isNull(companyName))
                    throw new ValidationException("Missing Company name in request", HttpStatus.UNPROCESSABLE_ENTITY);
                log.info("Request from Super Admin for jobs of Company : " + companyName);
                Company companyObjToUse = companyRepository.findByCompanyName(companyName);
                if (null == companyObjToUse)
                    throw new ValidationException("Company not found : " + companyName, HttpStatus.UNPROCESSABLE_ENTITY);
                jobsForCompany(responseBean, archived, companyObjToUse);
                break;
            default:
                jobsForLoggedInUser(responseBean, archived, loggedInUser);
        }
        log.info("Completed processing request to find all jobs for user in " + (System.currentTimeMillis() - startTime) + "ms");

        return responseBean;
    }

    private void jobsForLoggedInUser(JobWorspaceResponseBean responseBean, boolean archived, User loggedInUser) {
        if (archived) {
            responseBean.setListOfJobs(jobRepository.findByCreatedByAndDateArchivedIsNotNullOrderByCreatedOnDesc(loggedInUser));
            responseBean.setArchivedJobs(responseBean.getListOfJobs().size());
            responseBean.setOpenJobs((jobRepository.countByCreatedByAndDateArchivedIsNull(loggedInUser)).intValue());
        } else {
            responseBean.setListOfJobs(jobRepository.findByCreatedByAndDateArchivedIsNullOrderByCreatedOnDesc(loggedInUser));
            responseBean.setOpenJobs(responseBean.getListOfJobs().size());
            responseBean.setArchivedJobs((jobRepository.countByCreatedByAndDateArchivedIsNotNull(loggedInUser)).intValue());
        }
    }

    private void jobsForCompany(JobWorspaceResponseBean responseBean, boolean archived, Company company) {
        if (archived) {
            responseBean.setListOfJobs(jobRepository.findByCompanyIdAndDateArchivedIsNotNullOrderByCreatedOnDesc(company));
            responseBean.setArchivedJobs(responseBean.getListOfJobs().size());
            responseBean.setOpenJobs((jobRepository.countByCompanyIdAndDateArchivedIsNull(company)).intValue());
        } else {
            responseBean.setListOfJobs(jobRepository.findByCompanyIdAndDateArchivedIsNullOrderByCreatedOnDesc(company));
            responseBean.setOpenJobs(responseBean.getListOfJobs().size());
            responseBean.setArchivedJobs((jobRepository.countByCompanyIdAndDateArchivedIsNotNull(company)).intValue());
        }
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
        log.info("Received request to request to find a list of all candidates for job: " + jobCandidateMapping.getJob().getId() + " and stage: " + jobCandidateMapping.getStage().getId());
        long startTime = System.currentTimeMillis();
        //If the job is not published, do not process the request
        Job job = jobRepository.getOne(jobCandidateMapping.getJob().getId());


        if (null == job) {
            StringBuffer info = new StringBuffer("Invalid job id ").append(jobCandidateMapping.getJob().getId());
            log.info(info.toString());
            Map<String, String> breadCrumb = new HashMap<>();
            breadCrumb.put("Job Id ",jobCandidateMapping.getJob().getId().toString());
            breadCrumb.put("detail", info.toString());
            throw new WebException("Invalid job id " + jobCandidateMapping.getJob().getId(),  HttpStatus.UNPROCESSABLE_ENTITY, breadCrumb);
        }
        else {
            User loggedInUser = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(!loggedInUser.getRole().equals(IConstant.UserRole.Names.SUPER_ADMIN) && !job.getCompanyId().getId().equals(loggedInUser.getCompany().getId()))
                throw new WebException(IErrorMessages.JOB_COMPANY_MISMATCH, HttpStatus.UNAUTHORIZED);
            if(job.getStatus().equals(IConstant.JobStatus.DRAFT.getValue())) {
                StringBuffer info = new StringBuffer(IErrorMessages.JOB_NOT_LIVE).append(job.getStatus());
                log.info(info.toString());
                Map<String, String> breadCrumb = new HashMap<>();
                breadCrumb.put("Job Id", job.getId().toString());
                breadCrumb.put("detail", info.toString());
                throw new WebException(IErrorMessages.JOB_NOT_LIVE, HttpStatus.UNPROCESSABLE_ENTITY);
            }
        }

        SingleJobViewResponseBean responseBean = new SingleJobViewResponseBean();
        List<JobCandidateMapping> jcmList = jobCandidateMappingRepository.findByJobAndStage(jobCandidateMapping.getJob(), jobCandidateMapping.getStage());

        jcmList.forEach(jcmFromDb-> {
            jcmFromDb.setJcmCommunicationDetails(jcmCommunicationDetailsRepository.findByJcmId(jcmFromDb.getId()));
            Hibernate.initialize(jcmFromDb.getCandidate().getCandidateDetails());
            Hibernate.initialize(jcmFromDb.getCandidate().getCandidateCompanyDetails());
        });

        Collections.sort(jcmList);

        responseBean.setCandidateList(jcmList);

        List<Object[]> stageCountList = jobCandidateMappingRepository.findCandidateCountByStage(jobCandidateMapping.getJob().getId());

        stageCountList.stream().forEach(objArray -> {
            responseBean.getCandidateCountByStage().put(((Integer) objArray[0]).longValue(), ((BigInteger) objArray[1]).intValue());
        });
        log.info("Completed processing request to find candidates for job " + jobCandidateMapping.getJob().getId() + " and stage: " + jobCandidateMapping.getStage().getId() + " in "+ (System.currentTimeMillis() - startTime) + "ms");

        return responseBean;
    }

    private void addJobOverview(Job job, Job oldJob, User loggedInUser) { //method for add job for Overview page

        //validate title
        if (job.getJobTitle().length() > IConstant.TITLE_MAX_LENGTH)  //Truncate job title if it is greater than max length
            job.setJobTitle(job.getJobTitle().substring(0, IConstant.TITLE_MAX_LENGTH));

        Company userCompany = companyRepository.getOne(loggedInUser.getCompany().getId());
        if (null == userCompany) {
            throw new ValidationException("Cannot find company for logged in user", HttpStatus.EXPECTATION_FAILED);
        }
        job.setCompanyId(userCompany);

        if (null != oldJob) {//only update existing job
            oldJob.setCompanyJobId(job.getCompanyJobId());
            oldJob.setJobTitle(job.getJobTitle());
            oldJob.setJobDescription(job.getJobDescription());
            oldJob.setUpdatedBy(loggedInUser);
            oldJob.setUpdatedOn(new Date());
            //remove all data from job_key_skills and job_capabilities
            jobKeySkillsRepository.deleteByJobId(job.getId());
            jobCapabilitiesRepository.deleteByJobId(job.getId());

            jobRepository.save(oldJob);
        } else { //Create new entry for job
            job.setCreatedOn(new Date());
            job.setMlDataAvailable(false);
            job.setStatus(IConstant.JobStatus.DRAFT.getValue());
            job.setCreatedBy(loggedInUser);

            //End of code to be removed
            jobRepository.save(job);
        }
 //TODO: After current deployment to prod, uncomment the following
        try {
            callMl(new MLRequestBean(job.getJobTitle(), job.getJobDescription()), job.getId());
            if(null == oldJob) {
                job.setMlDataAvailable(true);
                jobRepository.save(job);
            }
            else {
                oldJob.setMlDataAvailable(true);
                jobRepository.save(oldJob);
            }
        } catch (Exception e) {
            log.error("Error while fetching data from ML: " + e.getMessage());
            job.setMlErrorMessage(IErrorMessages.ML_DATA_UNAVAILABLE);
        }
    }

    private void callMl(MLRequestBean requestBean, long jobId) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String mlResponse = RestClient.getInstance().consumeRestApi(objectMapper.writeValueAsString(requestBean), mlUrl, HttpMethod.POST,null);
        log.info("Response received: " + mlResponse);
        long startTime = System.currentTimeMillis();
        MLResponseBean responseBean = objectMapper.readValue(mlResponse, MLResponseBean.class);
        handleSkillsFromML(responseBean.getSkills(), jobId);
        handleCapabilitiesFromMl(responseBean.getSuggestedCapabilities(), jobId, true);
        handleCapabilitiesFromMl(responseBean.getRecommendedCapabilities(), jobId, false);
        log.info("Time taken to process ml data: " + (System.currentTimeMillis() - startTime) + "ms.");
    }

    /**
     * Method to handle all skills provided by ML
     *
     * @param skillsList List of skills obtained from ML
     * @param jobId the job id for which the skills have to persisted
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void handleSkillsFromML(List<Skills> skillsList, long jobId) throws Exception {
        log.info("Size of skill list: " + skillsList.size());
        List<JobKeySkills> jobKeySkillsToSave = new ArrayList<>(skillsList.size());
        skillsList.forEach(skill-> {
            //find a skill from the master table for the skill name provided
            SkillsMaster skillFromDb = skillMasterRepository.findBySkillName(skill.getName());
            //if none if found, add a skill
            if (null == skillFromDb) {
                skillFromDb = new SkillsMaster(skill.getName());
                skillMasterRepository.save(skillFromDb);
            }
            //add a record in job_key_skills with this skill id
            jobKeySkillsToSave.add(new JobKeySkills(skillFromDb, true,true, new Date(), (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(), jobId));
        });
        jobKeySkillsRepository.saveAll(jobKeySkillsToSave);
    }

    /**
     * Method to handle all capabilities provided by ML
     *
     * @param capabilitiesList
     * @param jobId
     * @param selectedByDefault
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void handleCapabilitiesFromMl(List<Capabilities> capabilitiesList, long jobId, boolean selectedByDefault) throws Exception {
        log.info("Size of capabilities list to process: " + capabilitiesList.size());
        if(null == mediumImportanceLevel)
            mediumImportanceLevel = findMasterDataForMediumImportance();
        List<JobCapabilities> jobCapabilitiesToSave = new ArrayList<>(capabilitiesList.size());
        capabilitiesList.forEach(capability->{
            jobCapabilitiesToSave.add(new JobCapabilities(capability.getCapability(), selectedByDefault, mediumImportanceLevel, new Date(), (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(), jobId));
        });
        jobCapabilitiesRepository.saveAll(jobCapabilitiesToSave);
    }

    private void addJobScreeningQuestions(Job job, Job oldJob, User loggedInUser) throws Exception { //method for add screening questions

        if (job.getJobScreeningQuestionsList().size() > MasterDataBean.getInstance().getConfigSettings().getMaxScreeningQuestionsLimit()) {
            throw new ValidationException(IErrorMessages.SCREENING_QUESTIONS_VALIDATION_MESSAGE + job.getId(), HttpStatus.BAD_REQUEST);
        }

        if (null != oldJob.getJobScreeningQuestionsList() && oldJob.getJobScreeningQuestionsList().size() > 0) {
            jobScreeningQuestionsRepository.deleteAll(oldJob.getJobScreeningQuestionsList());//delete old job screening question list
        }

        job.getJobScreeningQuestionsList().forEach(n -> {
            n.setCreatedBy(loggedInUser.getId());
            n.setCreatedOn(new Date());
            n.setJobId(job.getId());
        });
        jobScreeningQuestionsRepository.saveAll(job.getJobScreeningQuestionsList());

        //populate key skills for the job
        job.setJobKeySkillsList(jobKeySkillsRepository.findByJobId(job.getId()));
    }

    private void addJobKeySkills(Job job, Job oldJob, User loggedInUser) throws Exception { //update and add new key skill
        if (null != job.getJobKeySkillsList() && job.getJobKeySkillsList().isEmpty()) {
            throw new ValidationException("Job key skills " + IErrorMessages.EMPTY_AND_NULL_MESSAGE + oldJob.getId(), HttpStatus.BAD_REQUEST);
        }

        //delete all key skills where MlProvided=false
        List<JobKeySkills> userProvidedJobKeySkillslist = jobKeySkillsRepository.findByJobIdAndMlProvided(job.getId(), false);
        if (userProvidedJobKeySkillslist.size() > 0) {
            jobKeySkillsRepository.deleteAll(userProvidedJobKeySkillslist);
        }

        //For each keyskill in the request (will have only the mlProvided true ones), update the values for selected
        Map<Long, JobKeySkills> newSkillValues = new HashMap();
        job.getJobKeySkillsList().stream().forEach(jobKeySkill -> newSkillValues.put(jobKeySkill.getSkillId().getId(), jobKeySkill));

        oldJob.getJobKeySkillsList().forEach(oldKeySkill -> {
            if (oldKeySkill.getMlProvided()) {
                JobKeySkills newValue = newSkillValues.get(oldKeySkill.getSkillId().getId());
                oldKeySkill.setSelected(newValue.getSelected());
                oldKeySkill.setUpdatedOn(new Date());
                oldKeySkill.setUpdatedBy(loggedInUser);
            }
        });

        //get all skillMaster and tempskills master data
        Map<String, Long> skillsMasterMapByName = new HashMap<>();
        Map<Long, SkillsMaster> skillsMasterMap = new HashMap<>();
        List<SkillsMaster> skillsMasterList = skillMasterRepository.findAll();
        skillsMasterList.forEach(skillsMaster -> {
            skillsMasterMapByName.put(skillsMaster.getSkillName(), skillsMaster.getId());
            skillsMasterMap.put(skillsMaster.getId(), skillsMaster);
        });

        Map<String, Long> tempSkillsMapByName = new HashMap<>();
        Map<Long, TempSkills> tempSkillsMap = new HashMap<>();
        tempSkillsRepository.findAll().forEach(tempSkill -> {
            tempSkillsMapByName.put(tempSkill.getSkillName(), tempSkill.getId());
            tempSkillsMap.put(tempSkill.getId(), tempSkill);
        });

        //For each user entered key skill, do the following:
        //(i) check if the skill is already present in the ml key skill list for the job. If yes, skip this skill
        //(ii) if not, check if the skill is present in the skills master table. If yes, use that skill id to insert a record in the db
        //(iii) if not, check if the skill is present in the temp key skills table. If yes, use that id and insert a record in the db with temp key skill id column populated
        //(iv) if not, enter a record in the temp key skills table and use the id of the newly inserted record to insert a record in job key skill table with the temp key skill id column populated

        for (String userSkills : job.getUserEnteredKeySkill()) {

            if (skillsMasterMapByName.keySet().contains(userSkills)) {
                Long skillId = skillsMasterMapByName.get(userSkills);
                //does the skill match one of the those provided by ML?
                if (null != newSkillValues.get(skillId)) {
                    //found a match, skip this skill
                    continue;
                } else {
                    //no match found in mlProvided skill, add a record
                    jobKeySkillsRepository.save(new JobKeySkills(skillsMasterMap.get(skillId), false, true, new Date(), loggedInUser, job.getId()));
                }

            }
            //check if the user entered skill exists in the temp skills table
            else if (tempSkillsMapByName.keySet().contains(userSkills)) {
                Long tempSkillId = tempSkillsMapByName.get(userSkills);
                jobKeySkillsRepository.save(new JobKeySkills(tempSkillsMap.get(tempSkillId), false, true, new Date(), loggedInUser, job.getId()));

            }
            //this is a new skill, add to temp skills and refer to jobkeyskills table
            else {
                TempSkills tempSkills = tempSkillsRepository.save(new TempSkills(userSkills, false));
                jobKeySkillsRepository.save(new JobKeySkills(tempSkills, false, true, new Date(), loggedInUser, job.getId()));
            }
        }
        //populate the capabilities for the job
        job.setJobCapabilityList(jobCapabilitiesRepository.findByJobId(job.getId()));
    }


    private void addJobCapabilities(Job job, Job oldJob, User loggedInUser) { //add job capabilities

        if (null != job.getJobCapabilityList() && job.getJobCapabilityList().isEmpty()) {
            throw new ValidationException("Job Capabilities " + IErrorMessages.EMPTY_AND_NULL_MESSAGE + job.getId());
        }

        //For each capability in the request, update the values for selected and importance_level
        Map<Long, JobCapabilities> newCapabilityValues = new HashMap();
        job.getJobCapabilityList().stream().forEach(jobCapability -> newCapabilityValues.put(jobCapability.getId(), jobCapability));

        oldJob.getJobCapabilityList().forEach(oldCapability -> {
            JobCapabilities newValue = newCapabilityValues.get(oldCapability.getId());
            oldCapability.setImportanceLevel(newValue.getImportanceLevel());
            oldCapability.setSelected(newValue.getSelected());
            oldCapability.setUpdatedOn(new Date());
            oldCapability.setUpdatedBy(loggedInUser);
        });


        //29th July: Do not auto-publish the job. The job should be explicitly published by means of an API call
        //oldJob.setStatus(IConstant.JobStatus.PUBLISHED.getValue());
        //oldJob.setDatePublished(new Date());
        jobRepository.save(oldJob);

        job.getJobCapabilityList().clear();
        job.getJobCapabilityList().addAll(oldJob.getJobCapabilityList());
    }

    private void addJobDetail(Job job, Job oldJob, User loggedInUser) {//add job details
/*
        if (null == job.getJobDetail()) {
            throw new ValidationException("Job detail " + IErrorMessages.NULL_MESSAGE + job.getId(), HttpStatus.BAD_REQUEST);
        }

        //delete existing jobDetail record from the database
        jobDetailRepository.deleteByJobId(oldJob);

        MasterDataBean masterDataBean = MasterDataBean.getInstance();
        if (null == masterDataBean.getFunction().get(job.getJobDetail().getFunction().getId())) {
            throw new ValidationException("In Job detail, function " + IErrorMessages.NULL_MESSAGE + job.getId(), HttpStatus.BAD_REQUEST);
        }

        if (null == masterDataBean.getEducation().get(job.getJobDetail().getEducation().getId())) {
            throw new ValidationException("In Job detail, education " + IErrorMessages.NULL_MESSAGE + job.getId(), HttpStatus.BAD_REQUEST);
        }

        if (null == masterDataBean.getExpertise().get(job.getJobDetail().getExpertise().getId())) {
            throw new ValidationException("In Job detail, expertise " + IErrorMessages.NULL_MESSAGE + job.getId(), HttpStatus.BAD_REQUEST);
        }

        List<CompanyAddress> companyAddressList = companyAddressRepository.findByCompanyId(loggedInUser.getCompany().getId());
        List<CompanyBu> companyBuList = companyBuRepository.findByCompanyId(loggedInUser.getCompany().getId());

        Map<Long, CompanyBu> companyBuMap = new HashMap<>();
        Map<Long, CompanyAddress> companyAddressMap = new HashMap<>();

        companyBuList.forEach(companyBu -> companyBuMap.put(companyBu.getId(), companyBu));
        companyAddressList.forEach(companyAddress -> companyAddressMap.put(companyAddress.getId(), companyAddress));

        if (companyAddressList.isEmpty() || null == companyAddressMap.get(job.getJobDetail().getJobLocation().getId())
                || null == companyAddressMap.get(job.getJobDetail().getInterviewLocation().getId())) {
            throw new ValidationException("In Job detail, company address " + IErrorMessages.NULL_MESSAGE + job.getId(), HttpStatus.BAD_REQUEST);
        }

        if (companyBuList.isEmpty() || null == companyBuMap.get(job.getJobDetail().getBuId().getId())) {
            throw new ValidationException("In Job detail, company bu " + IErrorMessages.NULL_MESSAGE + job.getId(), HttpStatus.BAD_REQUEST);
        }

        String expRange = masterDataBean.getExperienceRange().get(job.getJobDetail().getExperienceRange().getId());

        if (null == expRange) {
            throw new ValidationException("In Job detail, experience Range " + IErrorMessages.NULL_MESSAGE + job.getId(), HttpStatus.BAD_REQUEST);
        }

        JobDetail detail = job.getJobDetail();
        String[] range = masterDataBean.getExperienceRange().get(job.getJobDetail().getExperienceRange().getId()).split(" ");
        detail.setMinExperience(Double.parseDouble(range[0]));
        detail.setMaxExperience(Double.parseDouble(range[2]));
        detail.setJobId(oldJob);
        detail.setCreatedBy(loggedInUser);
        detail.setCreatedOn(new Date());
        oldJob.setJobDetail(detail);
        oldJob.setUpdatedOn(new Date());

        jobRepository.save(oldJob);

        //populate all users for the company of current user
        List<User> userList = userRepository.findByCompanyId(loggedInUser.getCompany().getId());
        job.getUsersForCompany().addAll(userList);*/
    }

    private void addJobHiringTeam(Job job, Job oldJob, User loggedInUser) throws Exception {
/*
        List<User> userList = userRepository.findByCompanyId(loggedInUser.getCompany().getId());

        for (JobHiringTeam jobHiringTeam : job.getJobHiringTeamList()) {

            jobHiringTeam.setUserId(loggedInUser);//temp code for testing
            if (null == jobHiringTeam.getUserId() || !userList.contains(jobHiringTeam.getUserId())) {
                throw new ValidationException("Not valid User" + job.getId(), HttpStatus.BAD_REQUEST);
            }

            if (null == MasterDataBean.getInstance().getProcess().get(jobHiringTeam.getStageStepId().getStage().getId())) {
                throw new ValidationException("In Job hiring team, process " + IErrorMessages.NULL_MESSAGE + job.getId(), HttpStatus.BAD_REQUEST);

            }

            //TODO:Check Lead Recruiter and Hiring manager are selected or not

            job.getJobKeySkillsList().addAll(jobKeySkillsRepository.findByJobIdAndMlProvided(job.getId(), true));
            //job.getJobCapabilityList().addAll(jobCapabilitiesRepository.findByJobId(job.getId()));

            CompanyStageStep companyStageStep = jobHiringTeam.getStageStepId();

            companyStageStep = companyStageStepRepository.save(new CompanyStageStep(companyStageStep.getStep(), companyStageStep.getCompanyId(), companyStageStep.getStage(), new Date(), loggedInUser));
            jobHiringTeamRepository.save(new JobHiringTeam(oldJob.getId(), companyStageStep, jobHiringTeam.getUserId(), jobHiringTeam.getSequence(), new Date(), loggedInUser));
        }
*/
    }

    /**
     * Service method to publish a job
     *
     * @param jobId id of the job to be published
     */
    @Transactional
    public void publishJob(Long jobId) throws Exception {
        log.info("Received request to publish job with id: " + jobId);
        changeJobStatus(jobId,IConstant.JobStatus.PUBLISHED.getValue());
        log.info("Completed publishing job with id: " + jobId);
    }

    /**
     * Service method to archive a job
     *
     * @param jobId id of the job to be archived
     */
    @Transactional
    public void archiveJob(Long jobId) {
        log.info("Received request to archive job with id: " + jobId);
        changeJobStatus(jobId,IConstant.JobStatus.ARCHIVED.getValue());
        log.info("Completed archiving job with id: " + jobId);
    }

    /**
     * Service method to unarchive a job
     *
     * @param jobId id of the job to be unarchived
     */
    @Transactional
    public void unarchiveJob(Long jobId) throws Exception {
        log.info("Received request to unarchive job with id: " + jobId);
        changeJobStatus(jobId,null);
        log.info("Completed unarchiving job with id: " + jobId);
    }

    /**
     * common method to Publish, Archive or Unarchive a job
     * @param jobId the job on which the operation is to be performed
     * @param status the status to be set. If the job is being unarchived, the status will be sent as null
     */
    private void changeJobStatus(Long jobId, String status) {
        Job job = jobRepository.getOne(jobId);
        if (null == job) {
            throw new WebException("Job with id " + jobId + "does not exist", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if(null == status) {
            //check that the old status of job is archived
            if (!IConstant.JobStatus.ARCHIVED.getValue().equals(job.getStatus()))
                throw new WebException(IErrorMessages.JOB_NOT_ARCHIVED, HttpStatus.UNPROCESSABLE_ENTITY);
            if(null == job.getDatePublished())
                job.setStatus(IConstant.JobStatus.DRAFT.getValue());
            else
                job.setStatus(IConstant.JobStatus.PUBLISHED.getValue());

            job.setDateArchived(null);
        }
        else  {
            if (status.equals(IConstant.JobStatus.ARCHIVED.getValue()))
                job.setDateArchived(new Date());
            else
                job.setDatePublished(new Date());
            job.setStatus(status);
        }
        job.setUpdatedOn(new Date());
        job.setUpdatedBy((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        jobRepository.save(job);
    }


    public MasterData findMasterDataForMediumImportance() {
        final MasterData[] returnVal = {null};
        MasterDataBean.getInstance().getImportanceLevel().keySet().forEach( key -> {
            if(MasterDataBean.getInstance().getImportanceLevel().get(key).equals("Mid")) {
                returnVal[0] = masterDataRepository.getOne(key);
            }
        });
        return returnVal[0];
    }
}
