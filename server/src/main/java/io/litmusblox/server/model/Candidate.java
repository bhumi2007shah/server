/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 2:14 PM
 * Class Name : Candidate
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CANDIDATE")
public class Candidate implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotNull
    @Column(name = "LAST_NAME")
    private String lastName;

    @NotNull
    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="CREATED_BY")
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="UPDATED_BY")
    private User updatedBy;

    public String getDisplayName() {
        return firstName + " " + lastName;
    }

    @Transient
    @JsonProperty
    private String email;

    @Transient
    @JsonProperty
    private String mobile;

    @Transient
    @JsonProperty
    private String countryCode;

    @Transient
    @JsonProperty
    private String uploadErrorMessage;

    @Transient
    @JsonProperty
    private String candidateSource;

    @Transient
    @JsonProperty
    private String telephone;

    public Candidate(@NotNull String firstName, @NotNull String lastName, @NotNull Date createdOn, @NotNull User createdBy) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
    }

    public Candidate(@NotNull String firstName, @NotNull String lastName, String email, String mobile) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobile = mobile;
    }

    public Candidate() {
        super();
    }
}
