/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.service;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO to save workspace response for list of users
 *
 * @author : Shital Raval
 * Date : 1/8/19
 * Time : 1:00 PM
 * Class Name : UserWorkspaceBean
 * Project Name : server
 */
@Data
@NoArgsConstructor
public class UserWorkspaceBean {
    Long userId;
    String userName;
    String status;
    int numberOfJobsCreated;

    public UserWorkspaceBean(Long userId, String userName, String status) {
        this.userId = userId;
        this.userName = userName;
        this.status = status;
    }
}
