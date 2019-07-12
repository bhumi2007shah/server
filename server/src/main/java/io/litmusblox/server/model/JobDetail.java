/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 11:49 AM
 * Class Name : JobDetail
 * Project Name : server
 */
@Data
@Entity
@Table(name = "JOB_DETAIL")
public class JobDetail implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID")
    private Job jobId;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BU_ID")
    private CompanyBu buId;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUNCTION")
    private MasterData function;

    @NotNull
    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "MIN_SALARY")
    private Long minSalary;

    @Column(name = "MAX_SALARY")
    private Long maxSalary;

    @NotNull
    @Column(name = "MIN_EXPERIENCE")
    private Double minExperience;

    @NotNull
    @Column(name = "MAX_EXPERIENCE")
    private Double maxExperience;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDUCATION")
    private MasterData education;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_LOCATION")
    private CompanyAddress jobLocation;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERVIEW_LOCATION")
    private CompanyAddress interviewLocation;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXPERTISE")
    private MasterData expertise;

}
