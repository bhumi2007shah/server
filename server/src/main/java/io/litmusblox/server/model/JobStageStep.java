/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : Shital Raval
 * Date : 19/11/19
 * Time : 1:05 PM
 * Class Name : JobStageStep
 * Project Name : server
 */
@Entity
@Table(name = "JOB_STAGE_STEP")
@Data
public class JobStageStep implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "JOB_ID")
    private Long jobId;

    @NotNull
    @Column(name = "STAGE_STEP_ID")
    private Long stageStepId;
}