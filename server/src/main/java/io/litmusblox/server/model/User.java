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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @NotNull
    @Column(name = "FIRST_NAME")
    private String firstName;

    @NotNull
    @Column(name = "LAST_NAME")
    private String lastName;

    @NotNull
    @Column(name = "MOBILE")
    private String mobile;

    @NotNull
    @Column(name = "ROLE")
    private String role;

    @Column(name = "DESIGNATION")
    private String designation;

    @NotNull
    @Column(name = "STATUS")
    private String status;

    @NotNull
    //@OneToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name="COMPANY_ID")
    private Long companyId;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUNTRY_ID")
    private Country countryId;

    @NotNull
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

    public String getDisplayName() {
        return firstName + " " + lastName;
    }

}
