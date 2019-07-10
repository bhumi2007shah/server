/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 12:53 PM
 * Class Name : JobScreeningQuestions
 * Project Name : server
 */
@Data
@Entity
@Table(name = "JOB_SCREENING_QUESTIONS")
public class JobScreeningQuestions implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*@NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Job jobId;*/

    @NotNull
    @ManyToOne(cascade = {CascadeType.MERGE},fetch= FetchType.LAZY)
    @JoinColumn(name = "JOB_ID")
    private Job jobId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MASTER_SCREENING_QUESTION_ID")
    private ScreeningQuestions masterScreeningQuestionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_SCREENING_QUESTION_ID")
    private CompanyScreeningQuestion companyScreeningQuestionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_SCREENING_QUESTION_ID")
    private UserScreeningQuestion userScreeningQuestionId;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY")
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPDATED_BY")
    private User updatedBy;

}
