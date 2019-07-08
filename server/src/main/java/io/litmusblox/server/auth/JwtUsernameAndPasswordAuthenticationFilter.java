/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.auth;

/**
 * @author : sameer
 * Date : 8/7/19
 * Time : 10:13 AM
 * Class Name : JwtUsernameAndPasswordAuthenticationFilter
 * Project Name : server
 */
/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.litmusblox.server.model.User;
import io.litmusblox.server.security.JwtConfig;
import io.litmusblox.server.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtUsernameAndPasswordAuthenticationFilter.class);

    private AuthenticationManager authManager;

    private final JwtConfig jwtConfig;

    private final IUserService userService;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authManager, JwtConfig jwtConfig, IUserService userService){
        this.authManager = authManager;
        this.jwtConfig = jwtConfig;
        this.userService = userService;

        this.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(jwtConfig.getUri(), "POST"));

        logger.info("Initiating JwtUsernameAndPasswordAuthenticationFilter");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)throws AuthenticationException {
        try{

            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);

            List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                    .commaSeparatedStringToAuthorityList(user.getRole());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user.getEmail(), user.getPassword(), grantedAuthorities);

//            try {
//                User user1 = userService.getUserByEmail(user.getEmail());
//                user1.setPassword(null);
//                authToken.setDetails(user1);
//            }
//            catch (Exception e){
//                logger.info(e.getMessage(), e.getCause());
//            }

            return authManager.authenticate((authToken));
        }
        catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        Long now = System.currentTimeMillis();

//        Map<String, Object> userDetail = new HashMap<>();
//
//        userDetail.put("userDetail", auth.getDetails());
//        userDetail.put("authorities", auth.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        String token = Jwts.builder()
                .setSubject(auth.getName())
                .claim("authorities", auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                //.setClaims(userDetail)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExpiration()*1000))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                .compact();


        //response.addHeader(jwtConfig.getHeader(), jwtConfig.getPrefix()+token);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(token);
        response.getWriter().flush();
        response.getWriter().close();
    }
}