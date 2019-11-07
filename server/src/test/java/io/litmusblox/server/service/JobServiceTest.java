/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import io.litmusblox.server.AbstractTest;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.Job;
import io.litmusblox.server.model.MasterData;
import io.litmusblox.server.model.User;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test case for JobService
 *
 * @author : Shital Raval
 * Date : 7/11/19
 * Time : 1:42 PM
 * Class Name : JobServiceTest
 * Project Name : server
 */
@ActiveProfiles("test")
@NoArgsConstructor
@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
class JobServiceTest extends AbstractTest {
    @Autowired
    IJobService jobService;

    @org.junit.jupiter.api.Test
    void addJob() {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        boolean testPass = true;
        try {
            Job testJob = Job.builder()
                    .jobTitle("job title")
                    .jobDescription("job description")
                    .noOfPositions(1)
                    .mlDataAvailable(false)
                    .createdBy(User.builder().id(1L).build())
                    .createdOn(new Date())
                    .companyId(Company.builder().id(2L).build())
                    .education(MasterData.builder().id(15L).build())
                    .experienceRange(MasterData.builder().id(125L).build())
                    .expertise(MasterData.builder().id(122L).build())
                    .function(MasterData.builder().id(135L).build())
                    .currency("INR")
                    .usersForCompany(new ArrayList<>())
                    .build();
            jobService.addJob(testJob, IConstant.AddJobPages.overview.name());
            assertThat(testJob.getId()).isNotNull();
        } catch (Exception e) {
            e.printStackTrace();
            testPass = false;
        }

        assertThat(testPass).isTrue();
    }
}