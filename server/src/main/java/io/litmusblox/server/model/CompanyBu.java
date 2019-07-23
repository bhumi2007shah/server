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
 * Date : 3/7/19
 * Time : 6:16 PM
 * Class Name : CompanyBu
 * Project Name : server
 */
@Data
@Entity
@Table(name = "COMPANY_BU")
public class CompanyBu implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //@NotNull
    //@OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private Long companyId;

    @NotNull
    @Column(name = "BUSINESS_UNIT")
    private String businessUnit;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @NotNull
    //@OneToOne(fetch = FetchType.LAZY)
    @Column(name="CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    //@OneToOne(fetch = FetchType.LAZY)
    @Column(name="UPDATED_BY")
    private Long updatedBy;

}
