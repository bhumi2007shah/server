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

/**
 * @author : Sumit
 * Date : 3/7/19
 * Time : 6:25 PM
 * Class Name : CompanyStageStep
 * Project Name : server
 */
@Data
@Entity
@Table(name = "COMPANY_STAGE_STEP")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CompanyStageStep implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "STEP")
    private String step;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company companyId;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STAGE")
    private StageMaster stage;

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

    public CompanyStageStep(@NotNull String step, @NotNull Company companyId, @NotNull StageMaster stage, @NotNull Date createdOn, @NotNull User createdBy) {
        this.step = step;
        this.companyId = companyId;
        this.stage = stage;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
    }

    public CompanyStageStep() {
        super();
    }
}
