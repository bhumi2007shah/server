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
 * Time : 1:05 PM
 * Class Name : JobKeySkills
 * Project Name : server
 */
@Data
@Entity
@Table(name = "JOB_KEY_SKILLS")
public class JobKeySkills implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @NotNull
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

   // @Column(name = "SKILL_ID")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private SkillsMaster skillId;

  //  @Column(name = "SKILL_ID_FROM_TEMP")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private TempSkills skillIdFromTemp;

    @NotNull
    @Column(name = "ML_PROVIDED")
    private Boolean mlProvided;

    @NotNull
    @Column(name = "SELECTED")
    private Boolean selcted;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @NotNull
 //   @Column(name = "CREATED_BY")
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
