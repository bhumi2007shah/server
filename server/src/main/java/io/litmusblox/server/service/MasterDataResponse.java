/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import io.litmusblox.server.model.Country;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 2:26 PM
 * Class Name : MasterDataResponse
 * Project Name : server
 */
@Data
public class MasterDataResponse {
    private List<Country> countries = new ArrayList<Country>();
    private Map<Long,String> ImportanceLevel = new HashMap<>();
    private Map<Long,String> QuestionType  = new HashMap<>();
    private Map<Long,String> ExperienceRange  = new HashMap<>();
    private Map<Long,String> AddressType  = new HashMap<>();
    private Map<Long,String> Stage = new HashMap<>();
    private Map<Long,String> Process = new HashMap<>();
    private Map<Long,String> Function  = new HashMap<>();
    private Map<Long,String> Expertise  = new HashMap<>();
    private Map<Long,String> Education  = new HashMap<>();
}
