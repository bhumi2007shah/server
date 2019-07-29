/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author : Sumit
 * Date : 3/7/19
 * Time : 3:54 PM
 * Class Name : SkillsMaster
 * Project Name : server
 */
@Data
@Entity
@Table(name = "SKILLS_MASTER")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SkillsMaster implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "SKILL_NAME")
    private String skillName;
}
