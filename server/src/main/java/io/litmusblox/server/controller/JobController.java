/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */
package io.litmusblox.server.controller;

import io.litmusblox.server.model.Job;
import io.litmusblox.server.service.IJobService;
import io.litmusblox.server.service.JobResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class that exposes all REST endpoints for Job related operations
 *
 * @author : Shital Raval
 * Date : 1/7/19
 * Time : 2:09 PM
 * Class Name : JobController
 * Project Name : server
 */
@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    IJobService jobService;

    @PostMapping(value = "/createJob/{pageName}")
    JobResponseBean addJob(@RequestBody Job job, @PathVariable ("pageName") String pageName){
        return jobService.addJob(job, pageName);
    }
}