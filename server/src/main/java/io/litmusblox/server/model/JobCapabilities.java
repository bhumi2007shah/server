/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 1:53 PM
 * Class Name : JobCapabilities
 * Project Name : server
 */
@Data
@Entity
@Table(name = "JOB_CAPABILITIES")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@NoArgsConstructor
public class JobCapabilities implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "CAPABILITY_ID")
    private Long capabilityId;


    @NotNull
    @Column(name = "CAPABILITY_NAME")
    private String capabilityName;

    @NotNull
    @Column(name = "SELECTED")
    private Boolean selected;

    @NotNull
    @Column(name = "WEIGHTAGE")
    private int weightage;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date createdOn = new Date();

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CREATED_BY")
    @JsonIgnore
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date updatedOn = new Date();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPDATED_BY")
    @JsonIgnore
    private User updatedBy;

    @JoinColumn(name = "JOB_ID")
    private Long jobId;


    public JobCapabilities(@NotNull Long capabilityId, @NotNull String capabilityName, @NotNull Boolean selected, @NotNull int weightage, @NotNull Date createdOn, @NotNull User createdBy, Long jobId) {
        this.capabilityId = capabilityId;
        this.capabilityName = capabilityName;
        this.selected = selected;
        this.weightage = weightage;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.jobId = jobId;
    }
}
