/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.litmusblox.server.constant.IErrorMessages;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobDetail implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "JOB_ID")
    @JsonIgnore
    private Job jobId;

    @NotNull(message = "Company bu "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BU_ID")
    private CompanyBu buId;

    @NotNull(message = "Function "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FUNCTION")
    private MasterData function;

    @NotNull
    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "MIN_SALARY")
    private Long minSalary;

    @Column(name = "MAX_SALARY")
    private Long maxSalary;

    @Column(name = "MIN_EXPERIENCE")
    private Double minExperience;

    @Column(name = "MAX_EXPERIENCE")
    private Double maxExperience;

    @NotNull(message = "Education "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EDUCATION")
    private MasterData education;

    @NotNull(message = "Job location "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JOB_LOCATION")
    private CompanyAddress jobLocation;

    @NotNull(message = "Interview Location "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERVIEW_LOCATION")
    private CompanyAddress interviewLocation;

    @NotNull(message = "Expertise "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXPERTISE")
    private MasterData expertise;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="CREATED_BY")
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UPDATED_BY")
    private User updatedBy;

    @Transient
    private MasterData experienceRange;

}
