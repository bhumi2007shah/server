/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.litmusblox.server.psql.ListToArrayConverter;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author : Sumit
 * Date : 3/7/19
 * Time : 3:59 PM
 * Class Name : ScreeningQuestions
 * Project Name : server
 */
@Data
@Entity
@Table(name = "SCREENING_QUESTION")
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
public class ScreeningQuestions implements Serializable {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "QUESTION")
    private String question;

    @Column(name = "OPTIONS")
    @Convert(converter = ListToArrayConverter.class)
    private List<String> options;

    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QUESTION_TYPE")
    private MasterData questionType;

    @Type(type = "jsonb")
    @Column(name = "MULTILEVELOPTIONS",columnDefinition = "jsonb")
    private String multiLevelOptions;
}
