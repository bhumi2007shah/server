/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : Shital Raval
 * Date : 30/9/19
 * Time : 10:21 AM
 * Class Name : CreateJobPageSequence
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CREATE_JOB_PAGE_SEQUENCE")
public class CreateJobPageSequence implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "PAGE_NAME")
    private String pageName;

    @NotNull
    @Column(name = "PAGE_DISPLAY_NAME")
    private String pageDisplayName;

    @NotNull
    @Column(name = "PAGE_DISPLAY_ORDER")
    private int pageDisplayOrder;

    @JsonIgnore
    @Column(name = "DISPLAY_FLAG")
    private boolean displayFlag;

    @NotNull
    @Column(name = "SUBSCRIPTION_AVAILABILITY")
    private String subscriptionAvailability;
}
