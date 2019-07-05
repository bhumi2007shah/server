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
 * @author : oem
 * Date : 4/7/19
 * Time : 1:53 PM
 * Class Name : JobCapabilities
 * Project Name : server
 */
@Data
@Entity
@Table(name = "JOB_CAPABILITIES")
public class JobCapabilities implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @NotNull
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "CAPABILITY_NAME")
    private String capabilityName;

    @NotNull
    @Column(name = "SELECTED")
    private Boolean selected;

    @NotNull
   // @Column(name = "IMPORTANCE_LEVEL")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private MasterData importanceLevel;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @NotNull
   // @Column(name = "CREATED_BY")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

   // @Column(name = "UPDATED_BY")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User updatedBy;

}
