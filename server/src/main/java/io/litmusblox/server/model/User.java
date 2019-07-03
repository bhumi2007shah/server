/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */
package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Entity class for User table
 *
 * @author : Shital Raval
 * Date : 1/7/19
 * Time : 11:18 AM
 * Class Name : User
 * Project Name : server
 *
 */
@Data
@Entity
@Table(name = "USERS")
public class User implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "MOBILE")
    private String mobile;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "DESIGNATION")
    private String designation;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "CREATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn = new Date();

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

    @Column(name = "UPDATED_BY")
    private Long updatedBy;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Company companyId;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private Country countryId;

}
