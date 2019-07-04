/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 2:26 PM
 * Class Name : CandidateDetails
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CANDIDATE_DETAILS")
public class CandidateDetails implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @NotNull
    @Column(name = "CANDIDATE_ID")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Candidate candidateId;

    @Column(name = "DATE_OF_BIRTH")
    private Date dateOfBirth;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "MARITAL_STATUS")
    private String maritalStatus;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "PHYSICALLY_CHALLENGED")
    private Boolean physicallyChallenged;

    @Column(name = "CURRENT_ADDRESS")
    private String currentAddress;

    @Column(name = "LOCATION")
    private String location;

    @Column(name = "PREFERRED_LOCATIONS")
    private String preferredLocations;

    @Column(name = "PREFERRED_JOB_TYPE")
    private String preferredJobType;

    @Column(name = "PREFERRED_EMPLOYMENT_STATUS")
    private String preferredEmploymentStatus;

    @Column(name = "INDUSTRY")
    private String industry;

    @Column(name = "FUNCTIONAL_AREA")
    private String functionalArea;

    @Column(name = "TOTAL_EXPERIENCE")
    private Double totalExperience;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "KEY_SKILLS")
    private String keySkills;

    @Column(name = "RESUME_HEADLINE")
    private String resumeHeadline;

    @Column(name = "WORK_SUMMARY")
    private String workSummary;

    @Column(name = "OTHER_CERTIFICATES")
    private String otherCertificates;

    @Column(name = "CV_FILE_TYPE")
    private String cvFileType;

    @NotNull
    @Column(name = "CANDIDATE_SOURCE")
    private String candidateSource;

    @Column(name = "LAST_ACTIVE")
    private Date lastActive;

    @Column(name = "CANDIDATE_TYPE")
    private String candidateType;



}
