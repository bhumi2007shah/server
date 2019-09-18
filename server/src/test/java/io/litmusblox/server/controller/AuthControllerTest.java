package io.litmusblox.server.controller;

import io.litmusblox.server.model.Company;
import io.litmusblox.server.model.Country;
import io.litmusblox.server.model.User;
import io.litmusblox.server.security.JwtTokenUtil;
import io.litmusblox.server.security.LbAuthRequestFilter;
import io.litmusblox.server.service.LoginResponseBean;
import io.litmusblox.server.service.impl.LbUserDetailsService;
import io.litmusblox.server.utils.UsersUtil;
import lombok.NoArgsConstructor;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * @author : sameer
 * Date : 18/09/19
 * Time : 10:29 AM
 * Class Name : AuthControllerTest
 * Project Name : server
 */
@Ignore
@NoArgsConstructor
@RunWith(SpringRunner.class)
@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LbUserDetailsService lbUserDetailsService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        List<User> mockUserList= new UsersUtil().getUserList();
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }

    @Test
    public void login() throws Exception {
        User user = new User();
        user.setEmail("sameer@hexagonsearch.com");
        user.setPassword("123456");
        try {
            LoginResponseBean loginResponseBean = lbUserDetailsService.login(user);
            assertThat(loginResponseBean!=null).isTrue().toString();
            assertThat(loginResponseBean.getJwtToken()!=null).isTrue().toString();
        }
        catch(Exception e){
        }
    }

}
