/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.constant.IErrorMessages;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.ArrayList;
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
@JsonFilter("Job")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Job implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Length(max = IConstant.JOB_ID_MAX_LENGTH, message = "Company's Job ID: length should be between 0 and " + IConstant.JOB_ID_MAX_LENGTH)
    @Pattern(message = "COMPANY_JOB_ID "+IErrorMessages.ALPHANUMERIC_MESSAGE,regexp = IConstant.REGEX_FOR_COMPANY_JOB_ID)
    @Column(name = "COMPANY_JOB_ID")
    private String companyJobId;

    @NotNull(message = "Job title " + IErrorMessages.NULL_MESSAGE)
    @Pattern(message = "Job title "+IErrorMessages.SPECIAL_CHARACTER_MESSAGE, regexp = IConstant.REGEX_FOR_JOB_TITLE)
    @Column(name = "JOB_TITLE")
    private String jobTitle;

    //set default value 1
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
    @JsonIgnore
    private Company companyId;

    @Column(name = "DATE_PUBLISHED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePublished;;

    @NotNull
    @Column(name = "STATUS")
    private String status;

    @Column(name = "DATE_ARCHIVED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateArchived;

    @Column(name = "SCORING_ENGINE_JOB_AVAILABLE")
    private Boolean scoringEngineJobAvailable  = false;

    //@NotNull(message = "Company bu "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BU_ID")
    private CompanyBu buId;

    //@NotNull(message = "Function "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FUNCTION")
    private MasterData function;

    //@NotNull
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

    //@NotNull(message = "Education "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "EDUCATION")
    private MasterData education;

    //@NotNull(message = "Job location "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JOB_LOCATION")
    private CompanyAddress jobLocation;

   // @NotNull(message = "Interview Location "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERVIEW_LOCATION")
    private CompanyAddress interviewLocation;

    //@NotNull(message = "Expertise "+ IErrorMessages.NULL_MESSAGE)
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

    /*@OneToOne(cascade = CascadeType.MERGE,fetch = FetchType.LAZY, mappedBy = "jobId")
    private JobDetail jobDetail;*/

    @OneToMany(cascade = {CascadeType.MERGE},fetch = FetchType.LAZY,mappedBy = "jobId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<JobHiringTeam> jobHiringTeamList=new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE},fetch= FetchType.LAZY, mappedBy = "jobId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<JobScreeningQuestions> jobScreeningQuestionsList=new ArrayList<>();

    @OneToMany(/*cascade = {CascadeType.MERGE},*/fetch = FetchType.LAZY, mappedBy = "jobId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<JobKeySkills> jobKeySkillsList=new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE},fetch = FetchType.LAZY, mappedBy = "jobId")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<JobCapabilities> jobCapabilityList=new ArrayList<>();

    @Transient
    @JsonInclude
    private List<String> userEnteredKeySkill=new ArrayList<>();

    @Transient
    private List<User> usersForCompany=new ArrayList<>();

    @Transient
    private String companyName;

    @Transient
    private String mlErrorMessage;

    @Transient
    private MasterData experienceRange;
}
