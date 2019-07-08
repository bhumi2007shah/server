/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : oem
 * Date : 3/7/19
 * Time : 3:44 PM
 * Class Name : MasterData
 * Project Name : server
 */
@Data
@Entity
@Table(name = "MASTER_DATA")
public class MasterData implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "TYPE")
    private String type;

    @NotNull
    @Column(name = "VALUE")
    private String value;

}
