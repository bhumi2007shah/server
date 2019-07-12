/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import io.litmusblox.server.Constant.IConstant;
import io.litmusblox.server.Constant.IErrorMessages;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.*;

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
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Length(max = IConstant.JOB_ID_MAX_LENGTH)
    @Pattern(message = "Job_id "+IErrorMessages.ALPHANUMERIC_MESSAGE,regexp = IConstant.REGEX_FOR_JOB_ID)
    @Column(name = "COMPANY_JOB_ID")
    private String companyJobId;

    @NotNull(message = "Job title " + IErrorMessages.NULL_MESSAGE)
    @Pattern(message = "Job title "+IErrorMessages.SPECIAL_CHARACTER_MESSAGE, regexp = IConstant.REGEX_FOR_JOB_TITLE)
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    @NotNull(message = "No of positions " + IErrorMessages.NULL_MESSAGE)
    @Column(name = "NO_OF_POSITIONS")
    private Integer noOfPositions;

    @NotNull(message = "Job description " + IErrorMessages.NULL_MESSAGE)
    @Column(name = "JOB_DESCRIPTION")
    private String jobDescription;

    @NotNull
    @Column(name = "ML_DATA_AVAILABLE")
    private Boolean mlDataAvailable;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company companyId;

    @Column(name = "DATE_PUBLISHED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePublished;;

    @NotNull
    @Column(name = "STATUS")
    private String status = "Draft";

    @Column(name = "DATE_ARCHIVED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateArchived;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CREATED_BY")
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UPDATED_BY")
    private User updatedBy;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "jobId")
    private JobDetail jobDetail;

    @OneToMany(cascade = {CascadeType.MERGE},fetch= FetchType.LAZY, mappedBy = "jobId")
    private List<JobScreeningQuestions> jobScreeningQuestionsList=new ArrayList<>();

    @OneToMany(/*cascade = {CascadeType.MERGE},*/fetch = FetchType.LAZY, mappedBy = "jobId")
    private List<JobKeySkills> jobKeySkillsList=new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE},fetch = FetchType.LAZY, mappedBy = "jobId")
    private List<JobCapabilities> jobCapabilityList=new ArrayList<>();

    @Transient
    private Set<String> userEnteredKeySkill=new HashSet<>();

}
