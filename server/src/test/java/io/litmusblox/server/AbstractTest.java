/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server;

import io.litmusblox.server.model.User;
import io.litmusblox.server.service.IMasterDataService;
import io.litmusblox.server.service.MasterDataBean;
import io.litmusblox.server.service.impl.LbUserDetailsService;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
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

    @Autowired
    IMasterDataService masterDataService;

    @Autowired
    LbUserDetailsService lbUserDetailsService;

    public static String authKey = "";

    @Before
    @Transactional
    public final void setUp() {

        try {
            if (!MasterDataBean.getInstance().isLoaded()) {
                masterDataService.loadStaticMasterData();
            }

            authKey = lbUserDetailsService.login(User.builder().email("test@litmusblox.io").password("123456").build()).getJwtToken();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            log.fatal("Failed to load default Configuration.");
        }

    }

}
