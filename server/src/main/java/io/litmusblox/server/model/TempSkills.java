/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : Sumit
 * Date : 4/7/19
 * Time : 3:26 PM
 * Class Name : TempSkills
 * Project Name : server
 */
@Data
@Entity
@Table(name = "TEMP_SKILLS")
public class TempSkills implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "SKILL_NAME")
    private String skillName;

    @NotNull
    @Column(name = "REVIEWED")
    private Boolean reviewed;
}
