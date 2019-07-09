/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.security;

/**
 * @author : sameer
 * Date : 8/7/19
 * Time : 10:01 AM
 * Class Name : JwtTokenAuthenticationFilter
 * Project Name : server
 */

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;

    public JwtTokenAuthenticationFilter(JwtConfig jwtConfig){
        this.jwtConfig = jwtConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

            String header = request.getHeader(jwtConfig.getHeader());

            if (header == null || !header.startsWith(jwtConfig.getPrefix())) {
                chain.doFilter(request, response);
            }

            String token = header.replace(jwtConfig.getPrefix(), "");

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtConfig.getSecret().getBytes())
                        .parseClaimsJws(token)
                        .getBody();
                String userName = claims.getSubject();
                if (userName != null) {
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) claims.get("authorities");

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userName, null, roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())

                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                e.printStackTrace();
                SecurityContextHolder.clearContext();
            }

            chain.doFilter(request, response);
    }

}
