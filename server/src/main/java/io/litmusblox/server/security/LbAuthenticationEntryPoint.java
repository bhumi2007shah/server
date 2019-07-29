/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

/**
 * Class to throw back a 401 error for all unauthorized requests
 *
 * @author : shital
 * Date : 18/7/19
 * Time : 12:09 PM
 * Class Name : LbAuthenticationEntryPoint
 * Project Name : server
 */
@Component
public class LbAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}