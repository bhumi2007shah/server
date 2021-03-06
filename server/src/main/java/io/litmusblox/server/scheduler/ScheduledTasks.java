/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.scheduler;

import io.litmusblox.server.uploadProcessor.IProcessUploadedCV;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled tasks for the application
 *
 * @author : Shital Raval
 * Date : 16/7/19
 * Time : 2:45 PM
 * Class Name : ScheduledTasks
 * Project Name : server
 */
@Component
@Log4j2
public class ScheduledTasks {

    @Autowired
    IProcessUploadedCV processUploadedCV;

    @Scheduled(fixedRate = 30000, initialDelay = 5000)
    public void parseAndProcessCv() {
        processUploadedCV.processCv();
    }

    @Scheduled(fixedRate = 120000, initialDelay = 5000)
    public void rateAndProcessCv() {
        processUploadedCV.rateCv();
    }
}