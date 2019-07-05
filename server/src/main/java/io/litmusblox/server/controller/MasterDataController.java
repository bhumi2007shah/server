/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import io.litmusblox.server.service.IMasterDataService;
import io.litmusblox.server.service.MasterDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rest controller for master data
 *
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 2:09 PM
 * Class Name : MasterDataController
 * Project Name : server
 */
@RestController
@RequestMapping("/api/resources/masterdata")
public class MasterDataController {

    @Autowired
    IMasterDataService masterDataService;

    //Reload all master data
    @GetMapping(value="/reload")
    void reloadMasterData() throws Exception {
        masterDataService.reloadMasterData();
    }

    //Fetch individual master data items
    @PostMapping(value="/fetch/items")
    MasterDataResponse fetchForItems(@RequestBody List<String> requestItems) throws Exception {
        return masterDataService.fetchForItems(requestItems);
    }
}
