/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server.security;

/**
 * @author : sameer
 * Date : 8/7/19
 * Time : 9:53 AM
 * Class Name : JwtConfig
 * Project Name : server
 */

import org.springframework.beans.factory.annotation.Value;

public class JwtConfig {
    @Value("${security.jwt.uri:/api/auth/login}")
    private String Uri;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

    @Value("${security.jwt.expiration:#{24*60*60}}")
    private int expiration;

    @Value("${security.jwt.secret:JwtSecretKey}")
    private String secret;

    public String getUri() {
        return Uri;
    }

    public String getHeader() {
        return header;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getExpiration() {
        return expiration;
    }

    public String getSecret() {
        return secret;
    }


}