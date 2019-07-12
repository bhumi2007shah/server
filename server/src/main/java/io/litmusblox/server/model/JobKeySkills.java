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
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SKILL_ID")
    private SkillsMaster skillId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SKILL_ID_FROM_TEMP")
    private TempSkills skillIdFromTemp;

    @NotNull
    @Column(name = "ML_PROVIDED")
    private Boolean mlProvided;

    @NotNull
    @Column(name = "SELECTED")
    private Boolean selected;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY")
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPDATED_BY")
    private User updatedBy;

    @NotNull
  /*  @ManyToOne(cascade = {CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID")*/
    private Long jobId;

}
