/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import java.util.List;

/**
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 2:14 PM
 * Class Name : IMasterDataService
 * Project Name : server
 */
public interface IMasterDataService {
    /**
     * Method that will be called during application startup
     * Will read all master data from database and store them in internal cache
     *
     * @throws Exception
     */
    void loadStaticMasterData() throws Exception;

    /**
     * Method that will reload all master data in memory
     *
     * @throws Exception
     */
    void reloadMasterData() throws Exception;

    /**
     * Method to fetch specific master data values from cache
     *
     * @param fetchItemList The items for which master data needs to be fetched from memory
     * @return response bean with Maps containing requested master data values
     * @throws Exception
     */
    MasterDataResponse fetchForItems(List<String> fetchItemList) throws Exception;
}
