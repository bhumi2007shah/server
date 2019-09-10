/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : Sonal
 * Date : 9/9/19
 * Time : 2:09 PM
 * Class Name : CompanyHistory
 * Project Name : server
 */
@Data
@Entity
@Table(name = "COMPANY_HISTORY")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CompanyHistory implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "COMPANY_ID")
    private long companyId;

    @NotNull
    @Column(name = "DETAILS")
    private String details;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private User updatedBy;

    @NotNull
    @Column(name = "UPDATED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedOn = new Date();

}
