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
 * Time : 2:19 PM
 * Class Name : CandidateMobileHistory
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CANDIDATE_MOBILE_HISTORY")
public class CandidateMobileHistory implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Candidate candidateId;

    @NotNull
    @Column(name = "MOBILE")
    private String mobile;

    @NotNull
    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User createdBy;
}
