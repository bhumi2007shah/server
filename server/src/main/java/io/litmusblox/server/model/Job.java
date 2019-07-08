/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import io.litmusblox.server.Constant.IConstant;
import io.litmusblox.server.Constant.IErrorMessages;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Entity class for Job table
 *
 * @author : Shital Raval
 * Date : 2/7/19
 * Time : 9:40 AM
 * Class Name : Job
 * Project Name : server
 */
@Data
@Entity
@Table(name = "JOB")
public class Job implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Length(max = 10)
    @Pattern(message = "Job_id "+IErrorMessages.ALPHANUMERIC_MESSAGE,regexp = IConstant.REGEX_FOR_JOB_ID)
    @Column(name = "COMPANY_JOB_ID")
    private String companyJobId;

    @NotNull(message = "Job title " + IErrorMessages.NULL_MESSAGE)
    @Pattern(message = "Job title "+IErrorMessages.SPECIAL_CHARACTER_MESSAGE,regexp = IConstant.REGEX_FOR_JOB_TITLE)
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    @NotNull(message = "No of positions " + IErrorMessages.NULL_MESSAGE)
    @Pattern(message = "No of positions "+IErrorMessages.NUMERIC_MESSAGE,regexp = IConstant.REGEX_FOR_NO_OF_POSITIONS)
    @Column(name = "NO_OF_POSITIONS")
    private Long noOfPositions;

    @NotNull(message = "Job description " + IErrorMessages.NULL_MESSAGE)
    @Column(name = "JOB_DESCRIPTION")
    private String jobDescription;

    @NotNull
    @Column(name = "ML_DATA_AVAILABLE")
    private Boolean mlDataAvailable;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Company companyId;

    @Column(name = "DATE_PUBLISHED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePublished = new Date();

    @Column(name = "DATE_ARCHIVED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateArchived = new Date();

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User updatedBy;

    @NotNull(message = "Job screening questions " + IErrorMessages.NULL_MESSAGE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobId")
    private List<JobScreeningQuestions> jobScreeningQuestionsList;

    @NotNull(message = "Job key skills " + IErrorMessages.NULL_MESSAGE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobId")
    private List<JobKeySkills> jobKeySkillsList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobId")
    private List<JobCapabilities> jobCapabilityList;

}
