/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.controller;

import io.litmusblox.server.model.Country;
import io.litmusblox.server.service.IMasterDataService;
import io.litmusblox.server.service.MasterDataBean;
import io.litmusblox.server.service.MasterDataResponse;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
@RunWith(SpringRunner.class)
@WebMvcTest(value = MasterDataController.class, secure = false)
class MasterDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IMasterDataService masterDataService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {

        List<Country> mockList = new ArrayList<>();

        //add a list of countries
        Country mockCountry1 = Mockito.mock(Country.class);
        mockList.add(mockCountry1);
        Country mockCountry2 = Mockito.mock(Country.class);
        mockList.add(mockCountry2);

        MasterDataBean.getInstance().getCountryList().addAll(mockList);

    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @org.junit.jupiter.api.Test
    void reloadMasterData() {
    }

    @org.junit.jupiter.api.Test
    void fetchForItems() {
        try {
            MasterDataResponse responseObj = masterDataService.fetchForItems(Arrays.asList(new String[]{"Countries"}));
            assertThat(responseObj != null);
            assertThat(responseObj.getCountries() != null);
            assertThat(responseObj.getCountries().size() == 2);
        } catch (Exception e) {

        }
    }

    @org.junit.jupiter.api.Test
    void addMasterData() {
    }
}