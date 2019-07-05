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
    //@Column(name = "JOB_ID")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID")
    private Job jobId;

    @NotNull
   // @Column(name = "BU_ID")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private CompanyBu buId;

    @NotNull
  //  @Column(name = "FUNCTION")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
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
  //  @Column(name = "EDUCATION")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private MasterData education;

    @NotNull
  //  @Column(name = "JOB_LOCATION")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private CompanyAddress jobLocation;

    @NotNull
  //  @Column(name = "INTERVIEW_LOCATION")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private CompanyAddress interviewLocation;

    @NotNull
  //  @Column(name = "EXPERTISE")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private MasterData expertise;

    @NotNull
    @Column(name = "STATUS")
    private String status;

}
