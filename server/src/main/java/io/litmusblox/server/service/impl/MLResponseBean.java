/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service.impl;

import lombok.Data;

import java.util.List;

/**
 * POJO to map response from ML
 * The naming conventions in the classes below are purposely not being followed.
 * This is so that the response json from ML can be mapped directly to this class.
 *
 * @author : Shital Raval
 * Date : 27/8/19
 * Time : 12:15 PM
 * Class Name : MLResponseBean
 * Project Name : server
 */
@Data
public class MLResponseBean {
    private List<Skills> skills;
    private List<Capabilities> suggestedCapabilities;
    private List<Capabilities> recommendedCapabilities;
    private String occuranceOfDistinctIndustry;
    private List<Role> roles;
}

@Data
class Skills {
    private int id;
    private String name;
}

@Data
class Capabilities {
    private int capabilityWeight;
    private List<Keywords> keywords;
    private String capability;
    private int capScore;
    private String percentage;
    private String roleType;
    private int id;
}

@Data
class Keywords{
    private String keySkill;
    private int occurrence;
}

@Data
class Role {
    private String role;
    private int score;
    private String keywords;
    private String percentage;
    private int id;
}