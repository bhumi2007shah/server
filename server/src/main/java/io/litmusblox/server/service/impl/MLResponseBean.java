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
    private TowerGeneration towerGeneration;
    private RolePrediction rolePrediction;
}

@Data
class Skills {
    private int id;
    private String name;
    private int numberOfOccurrences;
}

@Data
class Capabilities {
    private int capCode;
    private String capability;
    private double score;
    private int capabilityWeight;
}

@Data
class Role {
    private String roleName;
    private int score;
}

@Data
class TowerGeneration {
    private List<Skills> skills;
    private List<String> roles;
    private List<Capabilities> suggestedCapabilities;
    private List<Capabilities> additionalCapabilities;
}

@Data
class RolePrediction {
    private List<Role> jdRoles;
    private List<Role> jtRoles;
    private String status;
}