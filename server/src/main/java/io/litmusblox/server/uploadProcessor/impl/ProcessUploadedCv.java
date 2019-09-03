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

import java.io.File;

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
        //TODO: Fetch all the CVs that need to be processed
        // for each cv, call the following with the pathTofile
        // rChilliCvProcessor.processFile();

        try{
            /*Stream<Path> filePathStream= Files.walk(Paths.get(environment.getProperty(IConstant.TEMP_REPO_LOCATION)));
            filePathStream.forEach(filePath -> {
                    if (Files.isRegularFile(filePath)) {
                        log.info("Temp folder Cv path : "+filePath.getFileName());
                        rChilliCvProcessor.processFile(filePath.toString());
                    }
                });*/



           // String [] files=Files.list(Paths.get(environment.getProperty(IConstant.TEMP_REPO_LOCATION)));
                    /*.forEach(filePath->{
                        //log.info("Temp folder Cv path : "+filePath.getFileName());
                        rChilliCvProcessor.processFile(filePath.toString());
                    });*/
                    File file=new File(environment.getProperty(IConstant.TEMP_REPO_LOCATION));
                    File [] files=file.listFiles();
                    //int i=0;
                    //if(files.length>0){
                    //for(int i=0; i<files.length; i++) {
                        log.info("Temp folder Cv path : "+files[0].getAbsoluteFile());
                        rChilliCvProcessor.processFile(files[0].getPath());
                        //i++;
                    //}
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Error while processing temp location files : "+e.getMessage());
        }
    }
}
