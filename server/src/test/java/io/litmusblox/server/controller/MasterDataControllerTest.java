/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import io.litmusblox.server.service.IMasterDataService;
import io.litmusblox.server.service.MasterDataBean;
import io.litmusblox.server.service.MasterDataResponse;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test case for MasterDataController
 *
 * @author : Shital Raval
 * Date : 5/7/19
 * Time : 6:42 PM
 * Class Name : MasterDataControllerTest
 * Project Name : server
 */
@ActiveProfiles("test")
@NoArgsConstructor
@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
class MasterDataControllerTest {
    @Autowired
    IMasterDataService masterDataService;

    void setup() {
        try {
            if (!MasterDataBean.getInstance().isLoaded()) {
                masterDataService.loadStaticMasterData();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.fatal("Failed to load default Configuration.");
        }
    }

    @org.junit.jupiter.api.Test
    void fetchForItems() {
        setup();
        try {
            MasterDataResponse responseObj = masterDataService.fetchForItems(Arrays.asList(new String[]{"countries"}));
            assertThat(responseObj != null).isTrue();
            assertThat(responseObj.getCountries() != null).isTrue();
            assertThat(responseObj.getCountries().size() > 0).isTrue();
        } catch (Exception e) {

        }
    }

    @org.junit.jupiter.api.Test
    void addMasterData() {
    }
}