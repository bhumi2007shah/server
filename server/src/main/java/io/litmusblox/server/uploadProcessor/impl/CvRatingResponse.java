/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.uploadProcessor.impl;

import lombok.Data;

import java.util.List;

/**
 * @author : Shital Raval
 * Date : 18/10/19
 * Time : 10:13 AM
 * Class Name : CvRatingResponse
 * Project Name : server
 */
@Data
public class CvRatingResponse {
    int overallRating;
    List<Keyword> keywords;
}

@Data
class Keyword {
    List<SupportingKeyword> supportingKeywords;
    String name;
    int rating;
    int occurrence;
}

@Data
class SupportingKeyword {
    String name;
    int occurrence;
}