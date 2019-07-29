/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Model class to hold various configuration settings like:
 * 1. max limits for daily usage by a single user
 * 2. max screening questions
 * 3. send communication email / sms flags
 *
 * @author : Shital Raval
 * Date : 29/7/19
 * Time : 10:44 AM
 * Class Name : ConfigurationSettings
 * Project Name : server
 */
@Data
@Entity
@Table(name="CONFIGURATION_SETTINGS")
public class ConfigurationSettings {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="CONFIG_NAME")
    private String configName;

    @Column(name="CONFIG_VALUE")
    private String configValue;
}
