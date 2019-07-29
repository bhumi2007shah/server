/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author : shital
 * Date : 18/7/19
 * Time : 11:58 AM
 * Class Name : LbAuthResponse
 * Project Name : server
 */
@Getter
@AllArgsConstructor
public class LbAuthResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    private final String jwttoken;

}
