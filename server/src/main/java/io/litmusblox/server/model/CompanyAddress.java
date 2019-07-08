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
 * Time : 5:08 PM
 * Class Name : CompanyAddress
 * Project Name : server
 */
@Data
@Entity
@Table(name = "COMPANY_ADDRESS")
public class CompanyAddress implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "LATITUDE")
    private Double latitude;

    @Column(name = "LONGITUDE")
    private Double longitude;

    @NotNull
/*    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="COMPANY_ID")*/
    private Long companyId;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ADDRESS_TYPE")
    private MasterData addressType;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CREATED_BY")
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UPDATED_BY")
    private User updatedBy;

}
