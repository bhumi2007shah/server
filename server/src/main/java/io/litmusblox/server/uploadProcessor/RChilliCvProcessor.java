/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.CvParsingDetailsRepository;
import io.litmusblox.server.repository.JobCandidateMappingRepository;
import io.litmusblox.server.repository.JobRepository;
import io.litmusblox.server.repository.UserRepository;
import io.litmusblox.server.service.IJobControllerMappingService;
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

import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class to process the CV uploaded against RChilli application
 * @author : shital
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
    IJobControllerMappingService jobControllerMappingService;

    @Autowired
    CvParsingDetailsRepository cvParsingDetailsRepository;


    /**
     * Service method to process the CV uploaded against RChilli application
     * @param filePath
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void processFile(String filePath) {
        Map<String, Object> map = findUserAndJob(filePath);
        User user=(User)map.get("user");
        Job job=(Job)map.get("job");

        String rchilliFormattedJson = null;
        ResumeParserDataRchilliBean bean = null;
        long rchilliResponseTime = 0L;
        boolean isCandidateFailedToProcess = false;

        RestClient rest = RestClient.getInstance();
        String jsonString = "{\"url\":\"" + environment.getProperty(IConstant.FILE_STORAGE_URL) + map.get("fileName").toString() + "\",\"userkey\":\"" + environment.getProperty(IConstant.USER_KEY) + "\",\"version\":\"" + environment.getProperty(IConstant.VERSION)
                + "\",\"subuserid\":\"" + environment.getProperty(IConstant.SUB_USER_ID) + "\"}";
        try {
            long startTime = System.currentTimeMillis();
            String rchilliJsonResponse=rest.consumeRestApi(jsonString, environment.getProperty(IConstant.RCHILLI_API_URL), HttpMethod.POST,null);
            rchilliResponseTime = System.currentTimeMillis() - startTime;
            log.info("Response taken come from Rchilli in : " + rchilliResponseTime + "ms.");
            rchilliJsonResponse=rchilliJsonResponse.substring(rchilliJsonResponse.indexOf(":")+1,rchilliJsonResponse.lastIndexOf("}"));
            rchilliFormattedJson=rchilliJsonResponse.substring(0, rchilliJsonResponse.indexOf("DetailResume")-7)+"\n"+"}";
            log.info("RchilliFormattedJson  : "+rchilliFormattedJson);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            bean = mapper.readValue(rchilliJsonResponse, ResumeParserDataRchilliBean.class);

        }catch(Exception e) {
            log.error("Error while getting rchiilli response : " + user.getEmail() + " : " + e.getMessage());
            isCandidateFailedToProcess=true;
        }

        try {

            if(isCandidateFailedToProcess) {
                storeFile(filePath, isCandidateFailedToProcess, job.getId(), null);
            }
        } catch (Exception ex) {
            log.error("Error while storing file : " + filePath + " : " + ex.getMessage(), HttpStatus.BAD_REQUEST);
        }

        //Add cv_parsing_details
        CvParsingDetails cvParsingDetails = new CvParsingDetails();
        cvParsingDetails.setCvFileName(filePath);
        cvParsingDetails.setProcessedOn(new Date());
        cvParsingDetails.setProcessingTime(rchilliResponseTime);
        cvParsingDetails.setParsingResponseHtml(bean.getHtmlResume());
        cvParsingDetails.setParsingResponseText(bean.getDetailResume());
        cvParsingDetails.setParsingResponseJson(rchilliFormattedJson);
        if(isCandidateFailedToProcess)
            cvParsingDetails.setProcessingStatus(IConstant.UPLOAD_STATUS.Failure.toString());

        cvParsingDetailsRepository.save(cvParsingDetails);
    }

    private Candidate setCandidateModel(ResumeParserDataRchilliBean bean, User user) {
        String mobile=bean.getFormattedMobile().isEmpty() ? bean.getFormattedPhone() : bean.getFormattedMobile();
        //Format mobile no
        mobile=Util.indianMobileConvertor(mobile);

        Candidate candidate=new Candidate(bean.getFirstName(),bean.getLastName(),bean.getEmail(), mobile,null,new Date(),null);
        candidate.setCandidateName(bean.getFullName());
        candidate.setCandidateSource(IConstant.CandidateSource.DragDropCv.toString());
        candidate.setCountryCode(user.getCountryId().getCountryCode());

        CandidateDetails candidateDetails=new CandidateDetails();
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
            CandidateEducationDetails candidateEducationDetails= new CandidateEducationDetails();

            if(educationSplit.getInstitution().getName().length()>75)
                candidateEducationDetails.setInstituteName(educationSplit.getInstitution().getName().substring(0,75));
            else
                candidateEducationDetails.setInstituteName(educationSplit.getInstitution().getName());

            candidateEducationDetails.setDegree(educationSplit.getDegree());
            if(!educationSplit.getEndDate().isEmpty()){
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
            candidateSkillDetails.setLastUsed(Util.convertStringToDate(skillSet.getLastUsed()));
            candidate.getCandidateSkillDetails().add(candidateSkillDetails);
        });
        return candidate;
    }

    /**
     * Method that will fetch all records from cv_parsing_details where status is null
     * and process them to create a job_candidate mapping
     */
    public void processRChilliData(){

        List<CvParsingDetails> recordsToProcess = cvParsingDetailsRepository.findByProcessingStatusIsNull();

        recordsToProcess.forEach(cvParsingDetails -> {
            try {
                Boolean isCandidateFailedToProcess=false;
                Map<String, Object> map = findUserAndJob(cvParsingDetails.getCvFileName());
                User user=(User)map.get("user");
                Job job=(Job)map.get("job");
                Date createdOn=(Date)map.get("createdOn");
                String fileName=map.get("fileName").toString();

                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                ResumeParserDataRchilliBean bean = mapper.readValue(cvParsingDetails.getParsingResponseJson(), ResumeParserDataRchilliBean.class);
                log.info("ResumeParserDataRchilliBean :"+bean);
                Candidate candidate = setCandidateModel(bean, user);
                int candidateProcessed = jobCandidateMappingRepository.getUploadedCandidateCount(createdOn, user);

                if (candidateProcessed >= MasterDataBean.getInstance().getConfigSettings().getCandidatesPerFileLimit()) {
                    log.error(IErrorMessages.MAX_CANDIDATE_PER_FILE_EXCEEDED + " : user id : " + user.getId());
                }
                //check for daily limit per user
                if (candidateProcessed >= MasterDataBean.getInstance().getConfigSettings().getDailyCandidateUploadPerUserLimit()) {
                    log.error(IErrorMessages.MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED + " : user id : " + user.getId());
                }
                try {
                    candidate = uploadDataProcessService.validateDataAndSaveJcmAndJcmCommModel(null, candidate, user, !candidate.getMobile().isEmpty(), job);
                    jobControllerMappingService.saveCandidateSupportiveInfo(candidate, user);
                    cvParsingDetails.setProcessingStatus(IConstant.UPLOAD_STATUS.Success.toString());
                }catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error While candidate process : "+bean.getEmail()+" "+"FileName : "+fileName+" : "+e.getStackTrace());
                    cvParsingDetails.setProcessingStatus(IConstant.UPLOAD_STATUS.Failure.toString());
                    isCandidateFailedToProcess=true;
                }
                storeFile(cvParsingDetails.getCvFileName(),isCandidateFailedToProcess,job.getId(),candidate.getId());

                CvParsingDetails cv=cvParsingDetailsRepository.save(cvParsingDetails);
                //log.info("CvParsingDetails : "+cv);
            } catch (IOException e) {
                log.error("Error While processing processRChilliData : "+cvParsingDetails.getCvFileName()+" : "+e.getMessage());
            } catch (Exception e) {
                log.error("Error While processing processRChilliData : " + cvParsingDetails.getCvFileName() + " : " + e.getMessage(), HttpStatus.BAD_REQUEST);
            }
        });

    }

    private Map<String, Object> findUserAndJob(String cvFileName){
        Map<String, Object> map=new HashMap<>();
        cvFileName = cvFileName.substring(cvFileName.lastIndexOf(File.separator) + 1);
        String[] s = cvFileName.split("_");
        long userId = Long.parseLong(s[0]);
        long jobId = Long.parseLong(s[1]);
        User user = userRepository.getOne(userId);
        Job job = jobRepository.getOne(jobId);
        Date createdOn = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        map.put("fileName", cvFileName);
        map.put("user", user);
        map.put("job", job);
        map.put("createdOn", createdOn);
        return map;
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
