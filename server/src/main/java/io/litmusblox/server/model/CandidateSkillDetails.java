/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author : Sumit
 * Date : 24/7/19
 * Time : 5:19 PM
 * Class Name : CandidateSkillDetails
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CANDIDATE_SKILL_DETAILS")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CandidateSkillDetails {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CANDIDATE_ID")
    private Long candidateId;

    @Column(name = "SKILL")
    private String skill;

    @Column(name = "LAST_USED")
    private Date lastUsed;
}
