/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@Entity
@Table(name = "TEMP_SKILLS")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TempSkills implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "SKILL_NAME")
    private String skillName;

    @NotNull
    @Column(name = "REVIEWED")
    private Boolean reviewed;

    public TempSkills(@NotNull String skillName, @NotNull Boolean reviewed) {
        this.skillName = skillName;
        this.reviewed = reviewed;
    }
}
