/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */
package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Company implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name="CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    @Column(name="UPDATED_BY")
    private Long updatedBy;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "companyId")
    private List<CompanyAddress> companyAddressList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "companyId")
    private List<CompanyBu> companyBuList;

}
