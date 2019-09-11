/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.ValidationException;
import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.CvParsingDetailsRepository;
import io.litmusblox.server.repository.JobCandidateMappingRepository;
import io.litmusblox.server.repository.JobRepository;
import io.litmusblox.server.repository.UserRepository;
import io.litmusblox.server.service.IJobCandidateMappingService;
import io.litmusblox.server.service.MasterDataBean;
import io.litmusblox.server.utils.RestClient;
import io.litmusblox.server.utils.StoreFileUtil;
import io.litmusblox.server.utils.Util;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Service class to process the CV uploaded against RChilli application
 *
 * @author : Shital Raval
 * Date : 21/8/19
 * Time : 1:06 PM
 * Class Name : DragAndDropCvProcessor
 * Project Name : server
 */
@Log4j2
@Service
public class RChilliCvProcessor {

    @Autowired
    Environment environment;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    IUploadDataProcessService uploadDataProcessService;

    @Autowired
    JobCandidateMappingRepository jobCandidateMappingRepository;

    @Autowired
    IJobCandidateMappingService jobCandidateMappingService;

    @Autowired
    CvParsingDetailsRepository cvParsingDetailsRepository;

    @Transactional(readOnly = true)
    User getUser(long userId) {
        return userRepository.findById(userId).get();
    }

    @Transactional(readOnly = true)
    Job getJob(long jobId) {
        return jobRepository.findById(jobId).get();
    }

    /**
     * Service method to process the CV uploaded against RChilli application
     *
     * @param filePath
     */
    public void processFile(String filePath) {
        // TODO:
        // 1. call the RChilli api to parse the candidate via RestClient
        // 2. from the name of file (<userId>_<jobId>_actualFileName), retrieve user Id and job id, to be used
        // 3. add jcm, and jcm communication details records
        // 4. increment the number of candidates processed by the user
        // 5. add a record in the new table cv_parsing_details with required details
        // 6. move the file to the job folder using the candidate id generated
        // In case of error from RChilli
        // 1. add record in cv_parsing_details <repolocation>/error_files/job_id

        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        String[] s = fileName.split("_");

        User user = getUser(Long.parseLong(s[0]));
        Job job = getJob(Long.parseLong(s[1]));

        Candidate candidate = null;
        String rchilliFormattedJson = null, rchilliJsonResponse = null;
        ResumeParserDataRchilliBean bean = null;
        long rchilliResponseTime = 0L;
        boolean isCandidateFailedToProcess = false, rChilliErrorResponse = false;

        RestClient rest = RestClient.getInstance();
        String jsonString = "{\"url\":\"" + environment.getProperty(IConstant.FILE_STORAGE_URL) + fileName + "\",\"userkey\":\"" + environment.getProperty(IConstant.USER_KEY) + "\",\"version\":\"" + environment.getProperty(IConstant.VERSION)
                + "\",\"subuserid\":\"" + environment.getProperty(IConstant.SUB_USER_ID) + "\"}";
        try {
            long startTime = System.currentTimeMillis();
            rchilliJsonResponse=rest.consumeRestApi(jsonString, environment.getProperty(IConstant.RCHILLI_API_URL), HttpMethod.POST,null);
            rchilliResponseTime = System.currentTimeMillis() - startTime;
            log.info("Recevied response from RChilli in " + rchilliResponseTime + "ms.");
            if(null != rchilliJsonResponse && rchilliJsonResponse.contains("errorcode") && rchilliJsonResponse.contains("errormsg")) {
                rChilliErrorResponse = true;
                isCandidateFailedToProcess = true;
            }

             if(!rChilliErrorResponse) {
                rchilliJsonResponse = rchilliJsonResponse.replace("{\n" +
                        "  \"ResumeParserData\" : ", "");

                rchilliFormattedJson = rchilliJsonResponse.substring(0, rchilliJsonResponse.indexOf(",\n" +
                        "    \"DetailResume\"")) + "\n" + "}";
                //log.info("RchilliJsonResponse  : "+rchilliJsonResponse);
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                bean = mapper.readValue(rchilliJsonResponse, ResumeParserDataRchilliBean.class);
                //log.info("ResumeParserDataRchilliBean :"+resumeParserDataRchilliBean);
                candidate = setCandidateModel(bean, user);

                isCandidateFailedToProcess = processCandidate(candidate, user, job);
            }
            else {
                log.error("Failed to process CV against RChilli: " + rchilliJsonResponse);
                 //TODO: Add sentry here?
            }

        } catch (Exception e) {
            log.error("Error while processing candidate in drag and drop : " + ((null != candidate) ? candidate.getEmail() : user.getEmail()) + " : " + e.getMessage(), HttpStatus.BAD_REQUEST);
            isCandidateFailedToProcess = true;
        }

        try {
            File file = new File(filePath);
            DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            int ret = input.read();
            while (ret != -1) {
                os.write(ret);
                ret = input.read();
            }
            os.flush();
            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
            if(isCandidateFailedToProcess && rChilliErrorResponse)
                StoreFileUtil.storeFile(multipartFile, job.getId(), environment.getProperty(IConstant.REPO_LOCATION), IConstant.ERROR_FILES, user.getId());
            else if (isCandidateFailedToProcess)
                StoreFileUtil.storeFile(multipartFile, job.getId(), environment.getProperty(IConstant.REPO_LOCATION), IConstant.ERROR_FILES, candidate.getId());
            else
                StoreFileUtil.storeFile(multipartFile, job.getId(), environment.getProperty(IConstant.REPO_LOCATION), IConstant.UPLOAD_TYPE.CandidateCv.toString(), candidate.getId());

            file.delete();
        } catch (Exception ex) {
            log.error("Error while save candidate resume in drag and drop : " + fileName + " : " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
        addCvParsingDetails(fileName, rchilliResponseTime, (null!=rchilliFormattedJson)?rchilliFormattedJson:rchilliJsonResponse, isCandidateFailedToProcess, bean);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private boolean processCandidate(Candidate candidate, User user, Job job) {

        int candidateProcessed = jobCandidateMappingRepository.getUploadedCandidateCount(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()), user);

        if (candidateProcessed >= MasterDataBean.getInstance().getConfigSettings().getCandidatesPerFileLimit()) {
            log.error(IErrorMessages.MAX_CANDIDATE_PER_FILE_EXCEEDED + " : user id : " + user.getId());
        }
        //check for daily limit per user
        if (candidateProcessed >= MasterDataBean.getInstance().getConfigSettings().getDailyCandidateUploadPerUserLimit()) {
            log.error(IErrorMessages.MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED + " : user id : " + user.getId());
        }
        try {
            candidate = uploadDataProcessService.validateDataAndSaveJcmAndJcmCommModel(null, candidate, user, !candidate.getMobile().isEmpty(), job);
            jobCandidateMappingService.saveCandidateSupportiveInfo(candidate, user);
        } catch (ValidationException ve) {
            log.error("Error while processing candidate information received from RChilli : " + ve.getMessage());
            return true;
        } catch (Exception e) {
            log.error("Error while processing candidate information received from RChilli : " + e.getMessage());
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void addCvParsingDetails(String fileName, long rchilliResponseTime, String rchilliFormattedJson, Boolean isCandidateFailedToProcess, ResumeParserDataRchilliBean bean) {
        try {
            //Add cv_parsing_details
            CvParsingDetails cvParsingDetails = new CvParsingDetails();
            cvParsingDetails.setCvFileName(fileName);
            cvParsingDetails.setProcessedOn(new Date());
            cvParsingDetails.setProcessingTime(rchilliResponseTime);
            if (isCandidateFailedToProcess)
                cvParsingDetails.setProcessingStatus(IConstant.UPLOAD_STATUS.Failure.toString());
            else
                cvParsingDetails.setProcessingStatus(IConstant.UPLOAD_STATUS.Success.toString());

            if (null != bean) {
                cvParsingDetails.setParsingResponseHtml(bean.getHtmlResume());
                cvParsingDetails.setParsingResponseText(bean.getDetailResume());
            }
            cvParsingDetails.setParsingResponseJson(rchilliFormattedJson);
            cvParsingDetailsRepository.save(cvParsingDetails);
        } catch (Exception e) {
            log.info("Save CvParsingDetails");
            e.printStackTrace();
        }
    }

    private Candidate setCandidateModel(ResumeParserDataRchilliBean bean, User user) {
        String mobile=bean.getFormattedMobile().isEmpty() ? bean.getFormattedPhone() : bean.getFormattedMobile();
        //Format mobile no
        mobile=Util.indianMobileConvertor(mobile);

        Candidate candidate = new Candidate(bean.getFirstName(), bean.getLastName(), bean.getEmail(), mobile, null, new Date(), null);
        candidate.setCandidateName(bean.getFullName());
        candidate.setCandidateSource(IConstant.CandidateSource.DragDropCv.toString());
        candidate.setCountryCode(user.getCountryId().getCountryCode());

        CandidateDetails candidateDetails = new CandidateDetails();
        candidateDetails.setDateOfBirth(Util.convertStringToDate(bean.getDateOfBirth()));
        candidateDetails.setGender(bean.getGender());

        if(bean.getSkills().length()>255)
            candidateDetails.setKeySkills(bean.getSkills().substring(0,255));
        else
            candidateDetails.setKeySkills(bean.getSkills());

        candidateDetails.setMaritalStatus(bean.getMaritalStatus());
        if(bean.getFormattedAddress().isEmpty())
            candidateDetails.setCurrentAddress(bean.getAddress());
        else
            candidateDetails.setCurrentAddress(bean.getFormattedAddress());

        candidate.setCandidateDetails(candidateDetails);

        bean.getSegregatedQualification().getEducationSplit().forEach(educationSplit -> {
            CandidateEducationDetails candidateEducationDetails = new CandidateEducationDetails();

            if (educationSplit.getInstitution().getName().length() > 75)
                candidateEducationDetails.setInstituteName(educationSplit.getInstitution().getName().substring(0, 75));
            else
                candidateEducationDetails.setInstituteName(educationSplit.getInstitution().getName());

            candidateEducationDetails.setDegree(educationSplit.getDegree());
            if (!educationSplit.getEndDate().isEmpty()) {
                candidateEducationDetails.setYearOfPassing(Util.getYearFromStringDate(educationSplit.getEndDate()));
            }
            candidate.getCandidateEducationDetails().add(candidateEducationDetails);
        });

        bean.getSegregatedExperience().getWorkHistory().forEach(workHistory -> {
            CandidateCompanyDetails candidateCompanyDetails = new CandidateCompanyDetails();
            candidateCompanyDetails.setCompanyName(workHistory.getEmployer());
            candidateCompanyDetails.setDesignation(workHistory.getJobProfile().getTitle());
            candidateCompanyDetails.setLocation(workHistory.getJobLocation().getEmployerCity());
            candidateCompanyDetails.setStartDate(Util.convertStringToDate(workHistory.getStartDate()));
            candidateCompanyDetails.setEndDate(Util.convertStringToDate(workHistory.getEndDate()));
            candidate.getCandidateCompanyDetails().add(candidateCompanyDetails);

            workHistory.getProjects().forEach(projects -> {
                CandidateProjectDetails candidateProjectDetails = new CandidateProjectDetails();
                candidateProjectDetails.setCompanyName(workHistory.getEmployer());
                candidateProjectDetails.setSkillsUsed(projects.getUsedSkills());
                candidate.getCandidateProjectDetails().add(candidateProjectDetails);
            });
        });

        bean.getWebSites().getWebsite().forEach(webSite -> {
            CandidateOnlineProfile candidateOnlineProfile = new CandidateOnlineProfile();
            candidateOnlineProfile.setUrl(webSite.getUrl());
            candidateOnlineProfile.setProfileType(webSite.getType());
            candidate.getCandidateOnlineProfiles().add(candidateOnlineProfile);
        });

        if(!bean.getLanguageKnown().isEmpty()){
            for (String language : bean.getLanguageKnown().split(",")) {
                CandidateLanguageProficiency candidateLanguageProficiency=new CandidateLanguageProficiency();
                candidateLanguageProficiency.setLanguage(language);
                candidate.getCandidateLanguageProficiencies().add(candidateLanguageProficiency);
            }
        }

        bean.getSkillKeywords().getSkillSet().forEach(skillSet -> {
            CandidateSkillDetails candidateSkillDetails=new CandidateSkillDetails();
            candidateSkillDetails.setSkill(skillSet.getSkill());
            candidateSkillDetails.setExpInMonths(Long.parseLong(skillSet.getExperienceInMonths()));
            candidateSkillDetails.setLastUsed(Util.convertStringToDate(skillSet.getLastUsed()));
            candidate.getCandidateSkillDetails().add(candidateSkillDetails);
        });
        return candidate;
    }

    private void storeFile(String filePath, Boolean isCandidateFailedToProcess, Long jobId, Long candidateId) throws Exception {

        File file=new File(filePath);
        DiskFileItem fileItem = new DiskFileItem("file", "text/plain", false, file.getName(), (int) file.length() , file.getParentFile());
        InputStream input = new FileInputStream(file);
        OutputStream os = fileItem.getOutputStream();
        int ret = input.read();
        while ( ret != -1 )
        {
            os.write(ret);
            ret = input.read();
        }
        os.flush();
        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        if(isCandidateFailedToProcess){
            StoreFileUtil.storeFile(multipartFile, jobId, environment.getProperty(IConstant.REPO_LOCATION), IConstant.ERROR_FILES,candidateId);
        }else{
            StoreFileUtil.storeFile(multipartFile, jobId, environment.getProperty(IConstant.REPO_LOCATION), IConstant.UPLOAD_TYPE.CandidateCv.toString(),candidateId);
        }
        file.delete();
    }
}
