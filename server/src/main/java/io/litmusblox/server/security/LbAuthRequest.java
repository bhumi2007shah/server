/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : shital
 * Date : 18/7/19
 * Time : 11:57 AM
 * Class Name : LbAuthRequest
 * Project Name : server
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LbAuthRequest implements Serializable {

    private static final long serialVersionUID = 5926468583005150707L;

    private String username;
    private String password;

}
