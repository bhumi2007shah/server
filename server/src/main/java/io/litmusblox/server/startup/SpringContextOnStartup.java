package io.litmusblox.server.startup;

import io.litmusblox.server.service.IMasterDataService;
import io.litmusblox.server.service.MasterDataBean;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Class that will perform actions required to be taken at application startup
 * e.g. loading master data in memory
 *
 * @author : Shital Raval
 * Date : 4/7/19
 * Time : 9:39 PM
 * Class Name : SpringContextOnStartup
 * Project Name : server
 */
@Component
@Log4j2
public class SpringContextOnStartup  implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    IMasterDataService masterDataService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
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
