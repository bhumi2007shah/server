/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobScreeningQuestions implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "JOB_ID")
    private Long jobId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MASTER_SCREENING_QUESTION_ID")
    private ScreeningQuestions masterScreeningQuestionId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMPANY_SCREENING_QUESTION_ID")
    private CompanyScreeningQuestion companyScreeningQuestionId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_SCREENING_QUESTION_ID")
    private UserScreeningQuestion userScreeningQuestionId;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdOn;

    @NotNull
    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date updatedOn;

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

}
