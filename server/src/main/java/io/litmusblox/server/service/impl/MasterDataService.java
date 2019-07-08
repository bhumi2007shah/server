/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.litmusblox.server.model.MasterData;
import io.litmusblox.server.model.UserScreeningQuestion;
import io.litmusblox.server.repository.CountryRepository;
import io.litmusblox.server.repository.MasterDataRepository;
import io.litmusblox.server.repository.UserScreeningQuestionRepository;
import io.litmusblox.server.service.IMasterDataService;
import io.litmusblox.server.service.MasterDataBean;
import io.litmusblox.server.service.MasterDataResponse;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Service class for Master Data handling
 *
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 2:18 PM
 * Class Name : MasterDataService
 * Project Name : server
 */
@Service
public class MasterDataService implements IMasterDataService {
    @Autowired
    MasterDataRepository masterDataRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    UserScreeningQuestionRepository screeningQuestionRepository;

    /**
     * Method that will be called during application startup
     * Will read all master data from database and store them in internal cache
     *
     * @throws Exception
     */
    @Override
    public void loadStaticMasterData() throws Exception {

        MasterDataBean.getInstance().getCountryList().addAll(countryRepository.findAll());

        List<MasterData> masterDataFromDb = masterDataRepository.findAll();

        //handle to the getter method of the map in the master data singleton instance class
        ConfigurablePropertyAccessor mapAccessor = PropertyAccessorFactory.forDirectFieldAccess(MasterDataBean.getInstance());

        //For every master data record from database, populate the corresponding map with key-value pairs
        masterDataFromDb.forEach(data -> {
            ((Map)mapAccessor.getPropertyValue(data.getType())).put(data.getId(), data.getValue());
        });

        MasterDataBean.getInstance().setLoaded(true);
    }

    /**
     * Method that will reload all master data in memory
     *
     * @throws Exception
     */
    @Override
    public void reloadMasterData() throws Exception {

        MasterDataBean masterBean = MasterDataBean.getInstance();

        //field accessor for MasterDataBean
        ConfigurablePropertyAccessor fieldAccessor = PropertyAccessorFactory.forDirectFieldAccess(masterBean);

        //Clear contents of all Map and List
        Field[] allFieldsOfMasterDataBean = masterBean.getClass().getDeclaredFields();
        for(Field f : allFieldsOfMasterDataBean) {
            if (f.getType().equals(java.util.List.class)) {
                ((List)fieldAccessor.getPropertyValue(f.getName())).clear();
            }
            else if (f.getType().equals(java.util.Map.class)) {
                ((Map)fieldAccessor.getPropertyValue(f.getName())).clear();
            }
            else
                continue;
        }
        loadStaticMasterData();
    }

    /**
     * Method to fetch specific master data values from cache
     *
     * @param fetchItemList The items for which master data needs to be fetched from memory
     * @return response bean with Maps containing requested master data values
     * @throws Exception
     */
    @Override
    public MasterDataResponse fetchForItems(List<String> fetchItemList) throws Exception {
        MasterDataResponse master = new MasterDataResponse();
        //populate data for each of the required items
        fetchItemList.stream().forEach(item -> getMasterData(master, item));
        return master;
    }

    /**
     * Method to add master data to database.
     * Supported master data types:
     * 1. UserScreeningQuestion
     *
     * @param jsonData       master data to be persisted (in json format)
     * @param masterDataType the type of master data to be persisted
     */
    @Transactional
    public void addMasterData(String jsonData, String masterDataType) throws Exception {
        switch (masterDataType) {
            case UserScreeningQuestion.IDENTIFIER:
                //create a Java object from the json string
                UserScreeningQuestion objToSave = new ObjectMapper().readValue(jsonData, UserScreeningQuestion.class);
                objToSave.setCreatedOn(new Date());
                //persist to database
                screeningQuestionRepository.save(objToSave);
                break;
            default:
                throw new Exception("Unsupported action");
        }
    }

    private static final String COUNTRY_MASTER_DATA = "Countries";

    /**
     * Method to fetch specific master data from cache
     * @param master the response bean to be populated
     * @param input the requested master data
     *
     */
    private void getMasterData(MasterDataResponse master, String input) {

        switch (input) {
            case COUNTRY_MASTER_DATA:
                master.getCountries().addAll(MasterDataBean.getInstance().getCountryList());
                break;
            default: //for all other properties, use reflection

                //handle to the getter method for the field
                ConfigurablePropertyAccessor fieldAccessor = PropertyAccessorFactory.forDirectFieldAccess(master);
                //handle to the getter method of the map in the master data singleton instance class
                ConfigurablePropertyAccessor mapAccessor = PropertyAccessorFactory.forDirectFieldAccess(MasterDataBean.getInstance());

                //add map from master data single instance to the response object
                ((Map)fieldAccessor.getPropertyValue(input)).putAll(
                        (Map) mapAccessor.getPropertyValue(input)
                );
        }
    }
}
