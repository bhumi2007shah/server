/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author : Sumit
 * Date : 30/08/19
 * Time : 12:28 AM
 * Class Name : CvParsingDetails
 * Project Name : server
 */
@Data
@Entity
@Table(name = "CV_PARSING_DETAILS")
public class CvParsingDetails {

    private static final long serialVersionUID = 6868521896546285046L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CV_FILE_NAME")
    private String cvFileName;

    @Column(name = "PROCESSED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date processedOn;

    @Column(name="PROCESSING_TIME")
    private Long processingTime;

    @Column(name="PROCESSING_STATUS")
    private String processingStatus;

    @Column(name = "PARSING_RESPONSE_JSON")
    private String parsingResponseJson;

    @Column(name = "PARSING_RESPONSE_TEXT")
    private String parsingResponseText;

    @Column(name = "PARSING_RESPONSE_HTML")
    private String parsingResponseHtml;
}
