/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import io.litmusblox.server.constant.IErrorMessages;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 11:49 AM
 * Class Name : JobDetail
 * Project Name : server
 */
@Data
@Entity
//@Embeddable
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//@DiscriminatorColumn(name = "JOB_ID")
//@Access(value=AccessType.FIELD)
//@DiscriminatorValue("JOB_DETAIL")
@Table(name = "JOB_DETAIL")
public class JobDetail implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @NotNull
    //@OneToOne(fetch = FetchType.LAZY)
    //@EmbeddedId
    @Column(name = "JOB_ID")
    private Long jobId;

    @NotNull(message = "Company bu "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BU_ID")
    private CompanyBu buId;

    @NotNull(message = "Function "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FUNCTION")
    private MasterData function;

    @NotNull
    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "MIN_SALARY")
    private Long minSalary;

    @Column(name = "MAX_SALARY")
    private Long maxSalary;

    @Column(name = "MIN_EXPERIENCE")
    private Double minExperience;

    @Column(name = "MAX_EXPERIENCE")
    private Double maxExperience;

    @NotNull(message = "Education "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EDUCATION")
    private MasterData education;

    @NotNull(message = "Job location "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_LOCATION")
    private CompanyAddress jobLocation;

    @NotNull(message = "Interview Location "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERVIEW_LOCATION")
    private CompanyAddress interviewLocation;

    @NotNull(message = "Expertise "+ IErrorMessages.NULL_MESSAGE)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EXPERTISE")
    private MasterData expertise;

    //@NotNull(message = "Experience Range "+ IErrorMessages.NULL_MESSAGE)
    @Transient
    private MasterData experienceRange;

    @Transient
    private List<User> userList=new ArrayList<>();

}
