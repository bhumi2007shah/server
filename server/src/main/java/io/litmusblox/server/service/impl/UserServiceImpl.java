package io.litmusblox.server.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.litmusblox.server.model.User;
import io.litmusblox.server.repository.UserRepository;
import io.litmusblox.server.security.JwtConfig;
import io.litmusblox.server.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements IUserService, UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JwtConfig jwtConfig;

    @Override
    public User getUserById(Long id){

        return userRepository.findById(id).orElse(new User());
    }

    @Override
    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException("User with email: "+email+" not found.");
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if( user == null ){
            throw new UsernameNotFoundException("User with email: "+email+" not found.");
        }

        List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_"+user.getRole());
        String password = user.getPassword();

        return new org.springframework.security.core.userdetails.User(email, password, auth);
    }

    @Override
    public String login(User requestUser) throws Exception {

        String jwtToken;
        if(null != requestUser && null!= requestUser.getEmail() && null!=requestUser.getPassword()) {
            User user = userRepository.findByEmail(requestUser.getEmail());
            if(null != user) {
                if (!user.getStatus().equalsIgnoreCase("Active")) {
                    throw new Exception("User is not Active");
                }

                if (encoder.matches(requestUser.getPassword(), user.getPassword())) {

                    Long now = System.currentTimeMillis();

                    Map<String, Object> map = new HashMap<>();

                    map.put("email", user.getEmail());
                    map.put("userId", user.getId());
                    map.put("firstName", user.getFirstName());
                    map.put("lastName", user.getLastName());
                    map.put("role", user.getRole());

                    jwtToken = Jwts.builder()
                            .setSubject(user.getEmail())
                            .setClaims(map)
                            .setIssuedAt(new Date(now))
                            .setExpiration(new Date(now + jwtConfig.getExpiration()*1000))
                            .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret().getBytes())
                            .compact();

                    return jwtToken;

                } else {
                    throw new Exception("Invalid Password and Email combination");
                }
            }
            else{
                throw new Exception("User not found with email: "+requestUser.getEmail());
            }
        }
        else{
            throw new Exception("Fields should not be null");
        }
    }

    @Bean
    public FilterChain chain(){
        return new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {

            }
        };
    }
}
