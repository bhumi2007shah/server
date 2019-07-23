/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author : Shital Raval
 * Date : 23/7/19
 * Time : 10:54 AM
 * Class Name : CandidateScreeningQuestionResponse
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CANDIDATE_SCREENING_QUESTION_RESPONSE")
@NoArgsConstructor
public class CandidateScreeningQuestionResponse {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "JOB_CANDIDATE_MAPPING_ID")
    private Long jobCandidateMappingId;

    @NotNull
    @Column(name = "JOB_SCREENING_QUESTION_ID")
    private Long jobScreeningQuestionId;

    @NotNull
    @Column(name = "RESPONSE")
    private String response;

    public CandidateScreeningQuestionResponse(@NotNull Long jobCandidateMappingId, @NotNull Long jobScreeningQuestionId, @NotNull String response) {
        this.jobCandidateMappingId = jobCandidateMappingId;
        this.jobScreeningQuestionId = jobScreeningQuestionId;
        this.response = response;
    }
}
