/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import io.litmusblox.server.Util;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.error.WebException;
import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.*;
import io.litmusblox.server.service.IJobControllerMappingService;
import io.litmusblox.server.service.UploadResponseBean;
import io.litmusblox.server.uploadProcessor.CsvFileProcessorService;
import io.litmusblox.server.uploadProcessor.ExcelFileProcessorService;
import io.litmusblox.server.uploadProcessor.IUploadDataProcessService;
import io.litmusblox.server.uploadProcessor.NaukriExcelFileProcessorService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Implementation class for methods exposed by IJobControllerMappingService
 *
 * @author : Shital Raval
 * Date : 16/7/19
 * Time : 4:56 PM
 * Class Name : JobControllerMappingService
 * Project Name : server
 */
@PropertySource("classpath:appConfig.properties")
@Log4j2
@Service
public class JobControllerMappingService implements IJobControllerMappingService {

    @Resource
    CandidateRepository candidateRepository;

    @Resource
    CandidateMobileHistoryRepository candidateMobileHistoryRepository;

    @Resource
    CandidateEmailHistoryRepository candidateEmailHistoryRepository;

    @Resource
    JobCandidateMappingRepository jobCandidateMappingRepository;

    @Resource
    UserRepository userRepository;

    @Resource
    MasterDataRepository masterDataRepository;

    @Resource
    JobRepository jobRepository;

    @Autowired
    IUploadDataProcessService iUploadDataProcessService;

    @Autowired
    Environment environment;



    /**
     * Service method to add a individually added candidates to a job
     *
     * @param candidates the list of candidates to be added
     * @param jobId      the job for which the candidate is to be added
     * @return the status of upload operation
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UploadResponseBean uploadIndividualCandidate(List<Candidate> candidates, Long jobId) throws Exception {
        //TODO: Add relevant code here
        //for each candidate, check if it is new or old
        //for the combination of jobid + candidateId, check that a record doesnot exist in jcm
        //else, add a jcm row with stage = Source

        UploadResponseBean uploadResponseBean = new UploadResponseBean();
        User u = userRepository.getOne(1L);
        Date createdOn=Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        int candidateProcessed=jobCandidateMappingRepository.getUploadedCandidateCount(createdOn,u);

        if (candidateProcessed >= Integer.parseInt(environment.getProperty(IConstant.MAX_CANDIDATES_PER_USER_PER_DAY))) {
            log.error(IErrorMessages.MAX_CANDIDATE_PER_FILE_EXCEEDED + " :: user id : " + u.getId() + " : not processing records");
            throw new WebException(IErrorMessages.MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        try {
            processCandidateData(candidates, uploadResponseBean, u, jobId, candidateProcessed);
        } catch (Exception ex) {
            log.error("Error while processing candidates uploaded :: " + ex.getMessage());
            uploadResponseBean.setStatus(IConstant.UPLOAD_STATUS.Failure.name());
        }
        return uploadResponseBean;
    }

    private void processCandidateData(List<Candidate> candidateList, UploadResponseBean uploadResponseBean, User u, Long jobId, int candidateProcessed){

        if (null != candidateList && candidateList.size() > 0) {
            iUploadDataProcessService.processData(candidateList, uploadResponseBean, candidateProcessed,jobId, !u.getCountryId().getCountryName().equalsIgnoreCase(IConstant.STR_INDIA));
        }
    }

    /**
     * Service method to add candidates from a file in one of the supported formats
     *
     * @param multipartFile the file with candidate information
     * @param jobId         the job for which the candidates have to be added
     * @param fileFormat    the format of file, for e.g. Naukri, LB format
     * @return the status of upload operation
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UploadResponseBean uploadCandidatesFromFile(MultipartFile multipartFile, Long jobId, String fileFormat) throws Exception {

        //validate the file source is supported by application
        if(!Arrays.asList(IConstant.UPLOAD_FORMATS_SUPPORTED.values()).contains(IConstant.UPLOAD_FORMATS_SUPPORTED.valueOf(fileFormat))) {
            log.error(IErrorMessages.UNSUPPORTED_FILE_SOURCE + fileFormat);
            throw new WebException(IErrorMessages.UNSUPPORTED_FILE_SOURCE + fileFormat, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        //validate that the file has an extension that is supported by the application
        Util.validateUploadFileType(multipartFile.getOriginalFilename());

        User u = userRepository.getOne(1L);
        Date createdOn=Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        UploadResponseBean uploadResponseBean = new UploadResponseBean();

        int candidatesProcessed = jobCandidateMappingRepository.getUploadedCandidateCount(createdOn, u);

        if (candidatesProcessed >= Integer.parseInt(environment.getProperty(IConstant.MAX_CANDIDATES_PER_USER_PER_DAY))) {
            log.error(IErrorMessages.MAX_CANDIDATE_PER_FILE_EXCEEDED + " :: user id : " + u.getId() + " : not saving file " + multipartFile);
            throw new WebException(IErrorMessages.MAX_CANDIDATES_PER_USER_PER_DAY_EXCEEDED, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        //Save file
        String fileName = storeFile(multipartFile, u.getId(), environment.getProperty(IConstant.REPO_LOCATION));
        log.info("User " + u.getDisplayName() + " uploaded " + fileName);
        List<Candidate> candidateList = processUploadedFile(fileName, uploadResponseBean, u, fileFormat, environment.getProperty(IConstant.REPO_LOCATION));

        try {
            processCandidateData(candidateList, uploadResponseBean, u, jobId, candidatesProcessed);

        } catch (Exception ex) {
            log.error("Error while processing file " + fileName + " :: " + ex.getMessage());
            uploadResponseBean.setStatus(IConstant.UPLOAD_STATUS.Failure.name());
        }

        return uploadResponseBean;
    }

    private String storeFile(MultipartFile multipartFile, long userId, String repoLocation) throws Exception {
        try {
            InputStream is = multipartFile.getInputStream();
            String filePath = getFileName(multipartFile.getOriginalFilename(), userId, repoLocation);
            Util.storeFile(is, filePath,repoLocation);
            return filePath;
        }
        catch (WebException e) {
            throw e;
        }
        catch (Exception e) {
            throw new WebException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,e);
        }
    }


    private String getFileName(String fileName, long userId, String repoLocation) throws Exception {

        try {
            String filePath = null;
            String staticRepoPath = null;
            if (Util.isNull(repoLocation)) {
                throw new WebException(IErrorMessages.INVALID_SETTINGS);
            }
            staticRepoPath = repoLocation;

            String time = Calendar.getInstance().getTimeInMillis() + "";
            filePath = "User" + File.separator + userId /*+ File.separator + userId*/;

            File file = new File(staticRepoPath + File.separator + filePath);
            if (!file.exists()) {
                file.mkdirs();
            }

            filePath = filePath + File.separator + fileName.substring(0,fileName.indexOf('.')) + "_" + userId + "_" + Util.formatDate(new Date(), IConstant.DATE_FORMAT_yyyymmdd_hhmm) + "." + Util.getFileExtension(fileName);
            return filePath;
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new WebException(IErrorMessages.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, e);
        }
    }

    private List<Candidate> processUploadedFile(String fileName, UploadResponseBean responseBean, User user, String fileSource, String repoLocation) {
        //code to parse through the records and save data in database
        String fileExtension = Util.getFileExtension(fileName).toLowerCase();
        List<Candidate> candidateList = null;
        switch (fileExtension) {
            case "csv":
                candidateList = new CsvFileProcessorService().process(fileName, responseBean, !user.getCountryId().getCountryName().equalsIgnoreCase(IConstant.STR_INDIA), repoLocation);
                break;
            case "xls":
            case "xlsx":
                switch (IConstant.UPLOAD_FORMATS_SUPPORTED.valueOf(fileSource)) {
                    case LitmusBlox:
                        candidateList = new ExcelFileProcessorService().process(fileName, responseBean, !user.getCountryId().getCountryName().equalsIgnoreCase(IConstant.STR_INDIA), repoLocation);
                        break;
                    case Naukri:
                        log.info("Reached the naukri parser");
                        candidateList = new NaukriExcelFileProcessorService().process(fileName, responseBean, !user.getCountryId().getCountryName().equalsIgnoreCase(IConstant.STR_INDIA), repoLocation);

                        break;
                }
                break;
            //TODO: add handling for files of xml type
            //case "xml":
            //    break;
            default:
                log.error(IErrorMessages.UNSUPPORTED_FILE_TYPE  + " - "+ fileExtension);
                throw new WebException(IErrorMessages.UNSUPPORTED_FILE_TYPE + " - " + fileExtension, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return candidateList;
    }

    /**
     * Service method to source and add a candidate from a plugin, for example Naukri plugin
     *
     * @param candidate the candidate to be added
     * @param jobId     the job for which the candidate is to be added
     * @return the status of upload operation
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UploadResponseBean uploadCandidateFromPlugin(Candidate candidate, Long jobId) throws Exception {
        //TODO: Add relevant code here
        return null;
    }
}
