/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */
package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : Sumit
 * Date : 3/7/19
 * Time : 2:53 PM
 * Class Name : Company
 * Project Name : server
 */
@Data
@Entity
@Table(name = "COMPANY")
public class Company implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "COMPANY_NAME")
    private String companyName;

    @NotNull
    @Column(name = "ACTIVE")
    private Boolean active;

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

    @NotNull
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "companyId")
    private List<CompanyAddress> companyAddressList;

    @NotNull
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "companyId")
    private List<CompanyBu> companyBuList;

}
