/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor.impl;

import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.uploadProcessor.IProcessUploadedCV;
import io.litmusblox.server.uploadProcessor.RChilliCvProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
}
