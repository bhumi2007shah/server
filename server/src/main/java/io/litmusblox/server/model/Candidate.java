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
    @NotNull
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    //@Column(name = "CREATED_BY")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    //@Column(name = "UPDATED_BY")
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User updatedBy;
}
