/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor;

import io.litmusblox.server.model.Candidate;
import io.litmusblox.server.service.UploadResponseBean;

import java.util.List;

/**
 * @author : Sumit
 * Date : 17/7/19
 * Time : 1:02 PM
 * Class Name : IUploadDataProcessService
 * Project Name : server
 */
public interface IUploadDataProcessService {

    void processData(List<Candidate> candidateList, UploadResponseBean uploadResponseBean, int candidateProcessed, Long jobId, boolean ignoreMobile);
}
