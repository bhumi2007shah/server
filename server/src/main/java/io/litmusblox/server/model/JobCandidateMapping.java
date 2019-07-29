/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Model class for JOB_CANDIDATE_MAPPING table
 *
 * @author : Shital Raval
 * Date : 10/7/19
 * Time : 2:15 PM
 * Class Name : JobCandidateMapping
 * Project Name : server
 */
@Data
@NoArgsConstructor
@Entity
@Table(name="JOB_CANDIDATE_MAPPING")
public class JobCandidateMapping implements Serializable {

    private static final long serialVersionUID = 6868521896546285047L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "JOB_ID")
    private Job job;

    @NotNull
    @OneToOne
    @JoinColumn(name = "CANDIDATE_ID")
    private Candidate candidate;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "STAGE")
    private MasterData stage;

    @NotNull
    @Column(name = "CANDIDATE_SOURCE")
    private String candidateSource;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="UPDATED_BY")
    private User updatedBy;

    @Column(name="JCM_UUID")
    @org.hibernate.annotations.Type(type = "pg-uuid")
    private UUID jcmUuid;

    @Column(name="CANDIDATE_INTEREST")
    private boolean candidateInterest;

    @Column(name = "CANDIDATE_INTEREST_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date candidateInterestDate;

    public JobCandidateMapping(@NotNull Job job, @NotNull Candidate candidate, @NotNull MasterData stage, @NotNull String candidateSource, @NotNull Date createdOn, @NotNull User createdBy, @NotNull UUID jcmUuid) {
        this.job = job;
        this.candidate = candidate;
        this.stage = stage;
        this.candidateSource = candidateSource;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.jcmUuid = jcmUuid;
    }
}