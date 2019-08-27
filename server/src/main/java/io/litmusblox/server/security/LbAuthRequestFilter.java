/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.litmusblox.server.constant.IConstant;
import io.litmusblox.server.service.impl.LbUserDetailsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Request filter implementation class
 *
 * @author : Shital Raval
 * Date : 18/7/19
 * Time : 12:05 PM
 * Class Name : LbAuthRequestFilter
 * Project Name : server
 */
@Component
@Log4j2
public class LbAuthRequestFilter extends OncePerRequestFilter {

    @Autowired
    private LbUserDetailsService lbUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        if(((HttpServletRequest)request).getMethod().equalsIgnoreCase("OPTIONS")) {

            ((HttpServletResponse)response).addHeader("Access-Control-Allow-Origin", "*");
            ((HttpServletResponse)response).addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
            ((HttpServletResponse)response).addHeader("Access-Control-Allow-Headers", "X-Custom-Header, X-Auth-Token, Content-Type,Authorization,Access-Control-Allow-Origin");
            ((HttpServletResponse)response).addHeader("Access-Control-Allow-Credentials", "true");
            return;
        }

        //no authorization required for options request sent by browser automatically
        if(!request.getMethod().equalsIgnoreCase("OPTIONS")) {
            final String requestTokenHeader = request.getHeader(IConstant.TOKEN_HEADER);

            String username = null;
            String jwtToken = null;
            // JWT Token begins with "Bearer ". Remove Bearer word and get only the Token value
            if (requestTokenHeader != null && requestTokenHeader.startsWith(IConstant.TOKEN_PREFIX)) {
                jwtToken = requestTokenHeader.substring(IConstant.TOKEN_PREFIX.length());
                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                } catch (IllegalArgumentException e) {
                    log.error("Unable to get JWT Token");
                } catch (ExpiredJwtException e) {
                    log.error("JWT Token has expired");
                }
            } else {
                log.error("JWT Token does not begin with Bearer String");
            }

            // Once we get the token validate it.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.lbUserDetailsService.loadUserByUsername(username);

                // if token is valid configure Spring Security to manually set
                // authentication
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // After setting the Authentication in the context, we specify
                    // that the current user is authenticated. So it passes the
                    // Spring Security Configurations successfully.
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
