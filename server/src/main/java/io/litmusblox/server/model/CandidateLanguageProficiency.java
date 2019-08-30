/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author : Sumit
 * Date : 24/7/19
 * Time : 5:14 PM
 * Class Name : CandidateLanguageProficiency
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CANDIDATE_LANGUAGE_PROFICIENCY")
@JsonFilter("CandidateLanguageProficiency")
public class CandidateLanguageProficiency implements Serializable, Cloneable{

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CANDIDATE_ID")
    private Long candidateId;

    @Column(name = "LANGUAGE")
    private String language;

    @Column(name = "PROFICIENCY")
    private String proficiency;
}
