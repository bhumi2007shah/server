/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor.impl;

import io.litmusblox.server.uploadProcessor.IProcessUploadedCV;
import io.litmusblox.server.uploadProcessor.RChilliCvProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class that has the methods to process the files and that will be triggered by scheduler
 *
 * @author : Shital Raval
 * Date : 21/8/19
 * Time : 1:09 PM
 * Class Name : ProcessUploadedCv
 * Project Name : server
 */
@Service
public class ProcessUploadedCv implements IProcessUploadedCV {

    @Autowired
    RChilliCvProcessor rChilliCvProcessor;

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
    }
}
