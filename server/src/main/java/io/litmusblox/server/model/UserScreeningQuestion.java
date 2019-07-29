/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 9:49 AM
 * Class Name : UserScreeningQuestion
 * Project Name : server
 */
@Data
@Entity
@Table(name = "USER_SCREENING_QUESTION")
@JsonFilter("UserScreeningQuestionFilter")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserScreeningQuestion implements Serializable {

    public static final String IDENTIFIER = "UserScreeningQuestion";

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "QUESTION")
    private String question;

    @Column(name = "OPTIONS")
    private String options;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private User userId;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTION_TYPE")
    private MasterData questionType;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

}
