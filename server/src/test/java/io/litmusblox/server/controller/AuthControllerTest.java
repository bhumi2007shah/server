package io.litmusblox.server.controller;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : sameer
 * Date : 18/09/19
 * Time : 10:29 AM
 * Class Name : AuthControllerTest
 * Project Name : server
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthControllerTest {
/*
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    JwtTokenUtil jwtTokenUtil;

    @MockBean
    LbUserDetailsService lbUserDetailsService;

    @Before
    public void setup(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    /*@org.junit.jupiter.api.BeforeEach
    void setUp() {
        List<User> userList = usersUtil.getUserList();

        when(lbUserDetailsService.login(new User())).thenReturn(userList);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
    }* /

    @Test
    @DisplayName("Test for login with valid user")
    public void login() throws Exception {
        User user = new User();

        user.setEmail("shital@hexagonsearch.com");
        user.setPassword("123456");

        ObjectMapper objectMapper = new ObjectMapper();

        String jsonRequest = objectMapper.writeValueAsString(user);
            /*LoginResponseBean loginResponseBean = lbUserDetailsService.login(user);
            assertThat(loginResponseBean!=null).isTrue();
            assertThat(loginResponseBean.getCompany()!=null).isTrue();*/
            /*when(lbUserDetailsService.login(Mockito.any(User.class))).thenReturn(new LoginResponseBean());* /
            MvcResult mvcResult = mockMvc.perform(post("/api/auth/login")
                    .content(jsonRequest)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            String result = mvcResult.getResponse().getContentAsString();
    }*/

    /*@org.junit.jupiter.api.Test
    @DisplayName("Test for login with invalid user")
    public void loginWithInvalidUser() throws Exception {
        String user = "{\"email\":\"sameer@hexagonsearch.com\", \"password\":\"\"}";
            *//*LoginResponseBean loginResponseBean = lbUserDetailsService.login(user);
            assertThat(loginResponseBean!=null).isTrue();
             assertThat(loginResponseBean.getCompany()!=null).isTrue();*//*
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(user)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized()
                );
    }*/
}
