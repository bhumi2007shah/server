/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server;

import io.litmusblox.server.service.IMasterDataService;
import io.litmusblox.server.service.MasterDataBean;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : Shital Raval
 * Date : 4/11/19
 * Time : 11:04 AM
 * Class Name : AbstractTest
 * Project Name : server
 */
@RunWith(SpringRunner.class)
@Log4j2
@ActiveProfiles("test")
public class AbstractTest {
    protected MockMvc mvc;

    @Autowired
    IMasterDataService masterDataService;

    @Before
    @Transactional
    public final void setUp() {

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

}
