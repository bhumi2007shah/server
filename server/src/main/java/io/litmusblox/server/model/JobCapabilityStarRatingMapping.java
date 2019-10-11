/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author : Shital Raval
 * Date : 10/10/19
 * Time : 10:18 AM
 * Class Name : JobCapabilityStarRatingMapping
 * Project Name : server
 */
@Data
@Entity
@Table(name = "JOB_CAPABILITY_STAR_RATING_MAPPING")
@NoArgsConstructor
public class JobCapabilityStarRatingMapping {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @NotNull
    @Column(name = "JOB_CAPABILITY_ID")
    @JsonIgnore
    private Long jobCapabilityId;

    @NotNull
    @Column
    @JsonIgnore
    private Long jobId;

    @NotNull
    @Column(name = "WEIGHTAGE")
    private int weightage;

    @NotNull
    @Column(name = "CUTOFF")
    private int cutoff;

    @NotNull
    @Column(name = "PERCENTAGE")
    private int percentage;

    @NotNull
    @Column(name = "STAR_RATING")
    private int starRating;

    public JobCapabilityStarRatingMapping(@NotNull Long jobCapabilityId, @NotNull Long jobId, @NotNull int weightage, @NotNull int cutoff, @NotNull int percentage, @NotNull int starRating) {
        this.jobCapabilityId = jobCapabilityId;
        this.jobId = jobId;
        this.weightage = weightage;
        this.cutoff = cutoff;
        this.percentage = percentage;
        this.starRating = starRating;
    }
}