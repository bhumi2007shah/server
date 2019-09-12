/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @author : Shital Raval
 * Date : 12/9/19
 * Time : 10:22 AM
 * Class Name : CandidateTechResponseData
 * Project Name : server
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "CANDIDATE_TECH_RESPONSE_DATA")
public class CandidateTechResponseData {
    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "JOB_CANDIDATE_MAPPING_ID")
    private JobCandidateMapping jobCandidateMappingId;

    @Column(name = "TECH_RESPONSE")
    private String techResponse;

    public CandidateTechResponseData(JobCandidateMapping jobCandidateMappingId) {
        this.jobCandidateMappingId = jobCandidateMappingId;
    }
}
