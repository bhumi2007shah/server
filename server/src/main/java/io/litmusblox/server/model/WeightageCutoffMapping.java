/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;

/**
 * @author : Sumit
 * Date : 01/10/19
 * Time : 6:18 PM
 * Class Name : WeightageCutoffMapping
 * Project Name : server
 */
@Data
@Entity
@Table(name = "WEIGHTAGE_CUTOFF_MAPPING")
public class WeightageCutoffMapping {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "WEIGHTAGE")
    private int weightage;

    @Column(name = "PERCENTAGE")
    private int percentage;

    @Column(name = "CUTOFF")
    private int cutoff;

    @Column(name = "STAR_RATING")
    private int starRating;
}

