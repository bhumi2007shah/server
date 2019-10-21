/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import io.litmusblox.server.model.CvParsingDetails;
import io.litmusblox.server.repository.CvParsingDetailsRepository;
import io.litmusblox.server.repository.JobKeySkillsRepository;
import io.litmusblox.server.service.impl.MLResponseBean;
import io.litmusblox.server.service.impl.MlCvRatingRequestBean;
import io.litmusblox.server.uploadProcessor.IProcessUploadedCV;
import io.litmusblox.server.uploadProcessor.RChilliCvProcessor;
import io.litmusblox.server.utils.RestClient;
import io.litmusblox.server.utils.SentryUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Class that has the methods to process the files and that will be triggered by scheduler
 *
 * @author : Shital Raval
 * Date : 21/8/19
 * Time : 1:09 PM
 * Class Name : ProcessUploadedCv
 * Project Name : server
 */
@Log4j2
@Service
public class ProcessUploadedCv implements IProcessUploadedCV {

    @Autowired
    RChilliCvProcessor rChilliCvProcessor;

    @Autowired
    Environment environment;

    @Resource
    CvParsingDetailsRepository cvParsingDetailsRepository;

    @Resource
    JobKeySkillsRepository jobKeySkillsRepository;

    /**
     * Method that will be called by scheduler
     *
     * @throws Exception
     */
    @Override
    public void processCv() {
        try{
            Stream<Path> filePathStream= Files.walk(Paths.get(environment.getProperty(IConstant.TEMP_REPO_LOCATION)));
            filePathStream.forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        log.info("Temp folder Cv path : "+filePath.getFileName());
                        rChilliCvProcessor.processFile(filePath.toString());
                        log.info("Completed processing " + filePath.toString());
                    }
                });
        } catch (Exception e) {
            log.info("Error while processing temp location files : "+e.getMessage());
        }
    }

    /**
     * Method that will be called by scheduler
     * All eligible records of CV will be run against CV rating api and rated
     */
    @Transactional
    public void rateCv() {
        List<CvParsingDetails> cvToRateList = cvParsingDetailsRepository.findByCvRatingApiFlagFalseAndParsingResponseTextNotNull();
        log.info("Found " + cvToRateList.size() + " records for CV rating process");

        cvToRateList.stream().forEach(cvToRate -> {
            //call rest api with the text part of cv

            List<String> jdKeySkills = jobKeySkillsRepository.findSkillNameByJobId(cvToRate.getJobCandidateMappingId().getJob().getId());
            if (jdKeySkills.size() == 0) {
                log.error("Found no key skills for " + cvToRate.getJobCandidateMappingId().getJob().getId());
                cvToRate.setCvRatingApiFlag(true);
                cvParsingDetailsRepository.save(cvToRate);
            }
            callCvRatingApi(new MlCvRatingRequestBean(jdKeySkills, cvToRate.getParsingResponseText()));
        });
    }

    private void callCvRatingApi(MlCvRatingRequestBean requestBean) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String mlResponse = RestClient.getInstance().consumeRestApi(objectMapper.writeValueAsString(requestBean), mlUrl, HttpMethod.POST,null);
        log.info("Response received: " + mlResponse);
        long startTime = System.currentTimeMillis();
        MLResponseBean responseBean = objectMapper.readValue(mlResponse, MLResponseBean.class);
        int numUniqueSkills = handleSkillsFromML(responseBean.getSkills(), jobId);
        if(numUniqueSkills != responseBean.getSkills().size()) {
            log.error(IErrorMessages.ML_DATA_DUPLICATE_SKILLS + mlResponse);
            Map breadCrumb = new HashMap<String, String>();
            breadCrumb.put("Job Id: ", String.valueOf(jobId));
            SentryUtil.logWithStaticAPI(null, IErrorMessages.ML_DATA_DUPLICATE_SKILLS + mlResponse, breadCrumb);
        }
        Set<Integer> uniqueCapabilityIds = new HashSet<>();
        handleCapabilitiesFromMl(responseBean.getSuggestedCapabilities(), jobId, true, uniqueCapabilityIds);
        handleCapabilitiesFromMl(responseBean.getAdditionalCapabilities(), jobId, false, uniqueCapabilityIds);
        log.info("Time taken to process ml data: " + (System.currentTimeMillis() - startTime) + "ms.");
    }
}
