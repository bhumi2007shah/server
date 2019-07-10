/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import io.litmusblox.server.service.IMasterDataService;
import io.litmusblox.server.service.MasterDataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Rest controller for master data. Exposes apis for the following functions:
 * 1. Fetch specific master data values
 * 2. Reload master data in server cache
 * 3. Save master data to corresponding table
 *
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 2:09 PM
 * Class Name : MasterDataController
 * Project Name : server
 */
@CrossOrigin
@RestController
@RequestMapping("/api/masterdata")
public class MasterDataController {

    @Autowired
    IMasterDataService masterDataService;

    /**
     * Reload all master data
     * @throws Exception
     */
    @GetMapping(value="/reload")
    void reloadMasterData() throws Exception {
        masterDataService.reloadMasterData();
    }

    /**
     * Fetch individual master data items
     *
     * @param requestItems
     * @return
     * @throws Exception
     */
    @PostMapping(value="/fetch/items")
    MasterDataResponse fetchForItems(@RequestBody List<String> requestItems) throws Exception {
        return masterDataService.fetchForItems(requestItems);
    }

    /**
     * Add master data value. Supported for the following types:
     * 1. UserScreeningQuestion
     *
     * @param jsonData data to persist
     * @param masterDataType the type of master data to be added
     */
    @PostMapping(value="/add/{masterDataType}")
    @ResponseStatus(value = HttpStatus.OK)
    void addMasterData(@RequestBody String jsonData, @PathVariable("masterDataType") String masterDataType) throws Exception {
        masterDataService.addMasterData(jsonData, masterDataType);
    }
}
