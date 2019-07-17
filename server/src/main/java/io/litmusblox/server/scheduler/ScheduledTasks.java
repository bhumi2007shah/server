/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.scheduler;

import io.litmusblox.server.model.*;
import io.litmusblox.server.repository.JobCapabilitiesRepository;
import io.litmusblox.server.repository.JobKeySkillsRepository;
import io.litmusblox.server.repository.JobRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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

    @Resource
    JobRepository jobRepository;

    @Resource
    JobKeySkillsRepository jobKeySkillsRepository;

    @Resource
    JobCapabilitiesRepository jobCapabilitiesRepository;

    @Scheduled(fixedRate = 3000, initialDelay = 5000)
    @Transactional(propagation = Propagation.REQUIRED)
    public void performMlApiCall() {
        log.info("ML Api call scheduled task trigerred");

        List<Job> jobsWithoutMlData = jobRepository.findByMlDataAvailable(false);

        jobsWithoutMlData.stream().forEach(job -> {

            //TODO: Replace the whole of this piece with actual ML api call

            User u = new User();
            u.setId(2L);

            String[] capabilityName = {"Java", "Dot Net", "Testing"};
            MasterData importance = new MasterData();
            importance.setId(104L);
            importance.setType("importanceLevel");

            for (int i = 0; i < 3; i++) {
                //add key skills
                SkillsMaster skillsMaster = new SkillsMaster();
                skillsMaster.setId(Integer.valueOf(i + 1).longValue());
                jobKeySkillsRepository.save(new JobKeySkills(skillsMaster, true, false, new Date(), u, job.getId()));

                //add capabilities
                JobCapabilities jc = new JobCapabilities();
                jc.setCapabilityName(capabilityName[i]);
                jc.setJobId(job.getId());
                jc.setSelected(false);
                jc.setImportanceLevel(importance);
                jc.setCreatedOn(new Date());
                jc.setCreatedBy(u);
                jobCapabilitiesRepository.save(jc);
            }

            job.setMlDataAvailable(true);
            job.setUpdatedOn(new Date());
            job.setUpdatedBy(u);
            jobRepository.save(job);

            log.info("Added job key skills and capabilities for job with id: " + job.getId());

            //TODO: End of code to be replaced

        });

    }
}
