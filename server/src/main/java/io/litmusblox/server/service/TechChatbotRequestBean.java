/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

/**
 * @author : Shital Raval
 * Date : 11/9/19
 * Time : 4:10 PM
 * Class Name : TechChatbotRequestBean
 * Project Name : server
 */
@Data
public class TechChatbotRequestBean {
    private UUID chatbotUuid;
    private String chatbotStatus;
    private Date chatbotLastUpdated;
    private int score;
    private String techResponseJson;
}

