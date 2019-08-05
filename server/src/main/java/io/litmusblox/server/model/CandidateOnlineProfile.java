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
 * Time : 5:05 PM
 * Class Name : CandidateOnlineProfile
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CANDIDATE_ONLINE_PROFILE")
@JsonFilter("CandidateOnlineProfile")
public class CandidateOnlineProfile {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CANDIDATE_ID")
    private Long candidateId;

    @Column(name = "PROFILE_TYPE")
    private String profileType;

    @Column(name = "URL")
    private String url;

}
