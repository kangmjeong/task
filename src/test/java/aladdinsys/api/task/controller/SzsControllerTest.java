//package aladdinsys.api.task.controller;
//
//import aladdinsys.api.task.dto.UserDTO;
//import aladdinsys.api.task.service.UserService;
//import aladdinsys.api.task.utils.jwt.JwtTokenUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.restdocs.RestDocumentationContextProvider;
//import org.springframework.restdocs.RestDocumentationExtension;
//import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import static org.mockito.BDDMockito.given;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(RestDocumentationExtension.class)
//@WebMvcTest(SzsController.class)
//@WithMockUser
//class SzsControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserService userService;
//
//    @MockBean
//    private JwtTokenUtil jwtTokenUtil;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    public void setUp(RestDocumentationContextProvider restDocumentation) {
//        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
//                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
//                .build();
//    }
//
//
//    @Test
//    void signUp() throws Exception {
//        UserDTO userDTO = new UserDTO("abcde12345", "abcde12345", "손오공", "820326-2715702");
//        String expectedResponse = "회원 가입이 완료되었습니다.";
//        given(userService.signUp(userDTO)).willReturn(expectedResponse);
//
//        // when // then
//        mockMvc.perform(post("/szs/signup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userDTO))
//                        // CSRF 토큰 추가
//                        .with(csrf()))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().string(expectedResponse))
//                .andDo(document("signup"));
//
//    }
//
//
//    @Test
//    void addAllowedUsers() {
//    }
//
//    @Test
//    void login() {
//    }
//
//    @Test
//    void userDetail() {
//    }
//
//    @Test
//    void updateUserDetails() {
//    }
//
//    @Test
//    void deleteUser() {
//    }
//}