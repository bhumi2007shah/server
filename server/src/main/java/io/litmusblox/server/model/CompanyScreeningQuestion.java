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
 * Time : 9:34 AM
 * Class Name : CompanyScreeningQuestion
 * Project Name : server
 */
@Data
@Entity
@Table(name = "COMPANY_SCREENING_QUESTION")
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
    private String options;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Company companyId;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private MasterData questionType;

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
}
