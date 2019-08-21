/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor;

import org.springframework.stereotype.Service;

/**
 * Service class to process the CV uploaded against RChilli application
 * @author : shital
 * Date : 21/8/19
 * Time : 1:06 PM
 * Class Name : DragAndDropCvProcessor
 * Project Name : server
 */
@Service
public class RChilliCvProcessor {


    /**
     * Service method to process the CV uploaded against RChilli application
     * @param file
     */
    public void processFile(String file) {
        // TODO:
        // 1. call the RChilli api to parse the candidate via RestClient
        // 2. from the name of file (<userId>_<jobId>_actualFileName), retrieve user Id and job id, to be used
        // 3. add jcm, and jcm communication details records
        // 4. increment the number of candidates processed by the user
        // 5. add a record in the new table cv_parsing_details with required details
        // 6. move the file to the job folder using the candidate id generated
        // In case of error from RChilli
        // 1. add record in cv_parsing_details <repolocation>/job_id/error_files/
    }
}
