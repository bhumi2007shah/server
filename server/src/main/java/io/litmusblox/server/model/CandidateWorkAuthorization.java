/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Data;

import javax.persistence.*;

/**
 * @author : Sumit
 * Date : 24/7/19
 * Time : 5:12 PM
 * Class Name : CandidateWorkAuthorization
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CANDIDATE_WORK_AUTHORIZATION")
@JsonFilter("CandidateWorkAuthorization")
public class CandidateWorkAuthorization {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CANDIDATE_ID")
    private Long candidateId;

    @Column(name = "COUNTRY_NAME")
    private String countryName;

    @Column(name = "AUTHORIZED_TO_WORK")
    private boolean authorizedToWork;
}
