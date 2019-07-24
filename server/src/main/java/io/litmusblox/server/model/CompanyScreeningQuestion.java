/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import io.litmusblox.server.psql.ListToArrayConverter;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 9:34 AM
 * Class Name : CompanyScreeningQuestion
 * Project Name : server
 */
@Data
@Entity
@Table(name = "COMPANY_SCREENING_QUESTION")
@JsonFilter("CompanyScreeningQuestionFilter")
public class CompanyScreeningQuestion implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "QUESTION")
    private String question;

    @Column(name = "OPTIONS")
    @Convert(converter = ListToArrayConverter.class)
    private List<String> options;

    @JoinColumn(name = "COMPANY_ID")
    private Long companyId;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTION_TYPE")
    private MasterData questionType;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @NotNull
    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    @Column(name="UPDATED_BY")
    private Long updatedBy;
}
