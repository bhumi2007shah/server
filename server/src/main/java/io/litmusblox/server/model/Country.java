/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author : Sumit
 * Date : 3/7/19
 * Time : 3:04 PM
 * Class Name : Country
 * Project Name : server
 */
@Data
@Entity
@Table(name = "COUNTRY")
public class Country implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "COUNTRY_NAME")
    private String countryName;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "MAX_MOBILE_LENGTH")
    private Long maxMobileLength;

}
