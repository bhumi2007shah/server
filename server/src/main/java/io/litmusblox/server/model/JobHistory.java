/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

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
 * Time : 2:09 PM
 * Class Name : JobHistory
 * Project Name : server
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "JOB_HISTORY")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobHistory implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "JOB_ID")
    private long jobId;

    @NotNull
    @Column(name = "DETAILS")
    private String details;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UPDATED_BY")
    private User updatedBy;

    @NotNull
    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    public JobHistory(@NotNull long jobId, @NotNull String details, @NotNull User updatedBy) {
        this.jobId = jobId;
        this.details = details;
        this.updatedBy = updatedBy;
    }
}
