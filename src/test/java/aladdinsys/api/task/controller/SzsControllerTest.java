package aladdinsys.api.task.controller;

import aladdinsys.api.task.ApiDocumentUtils;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.service.UserService;
import aladdinsys.api.task.utils.jwt.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@WithMockUser
class SzsControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("POST /szs/signup")
    void signUp() throws Exception {
        UserDTO validUser = new UserDTO("abcde123456", "abcde123456", "홍길동", "860824-1655068");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(validUser);

        ResultActions resultActions = mockMvc.perform(post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("저장이 완료되었습니다."))
                .andDo(document("{class-name}/{method-name}",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                        )
                ));

        String jsonResponse = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(JsonPath.<Boolean>read(jsonResponse, "$.success")).isEqualTo(true);
        assertThat(JsonPath.<String>read(jsonResponse, "$.message")).isEqualTo("저장이 완료되었습니다.");
    }

    @Test
    @DisplayName("POST /szs/login")
    void login() throws Exception {
        UserDTO validUser = new UserDTO("abcde123456", "abcde123456", null, null);
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(validUser);

        ResultActions resultActions = mockMvc.perform(post("/szs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인 성공"))
                .andExpect(jsonPath("$.token").exists())
                .andDo(document("{class-name}/{method-name}",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                                fieldWithPath("token").type(JsonFieldType.STRING).description("JWT 토큰")
                        )));

        String jsonResponse = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(JsonPath.<Boolean>read(jsonResponse, "$.success")).isEqualTo(true);
        assertThat(JsonPath.<String>read(jsonResponse, "$.message")).isEqualTo("로그인 성공");
        assertThat(JsonPath.<String>read(jsonResponse, "$.token")).isNotNull();

    }

    @Test
    @DisplayName("GET /szs/user-detail")
    void userDetail() throws Exception {

        String token = jwtTokenUtil.generateToken(new UserDTO("abcde123456", "abcde123456", null, null));

        MvcResult userDetailsResult = mockMvc.perform(get("/szs/user-detail")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(document("{class-name}/{method-name}",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("user.userId").type(JsonFieldType.STRING).description("사용자 아이디"),
                                fieldWithPath("user.password").type(JsonFieldType.STRING).description("사용자 비밀번호"),
                                fieldWithPath("user.name").type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("user.regNo").type(JsonFieldType.STRING).description("사용자 주민등록번호")
                        )
                ))
                .andReturn();

        String userDetailsResponse = userDetailsResult.getResponse().getContentAsString();

        assertThat(JsonPath.<Boolean>read(userDetailsResponse, "$.success")).isEqualTo(true);
        assertThat(JsonPath.<String>read(userDetailsResponse, "$.user.userId")).isEqualTo("abcde123456");

    }

    @Test
    @DisplayName("GET /szs/modify-user")
    void updateUserDetails() throws Exception {
        String mockJwtToken = jwtTokenUtil.generateToken(new UserDTO("abcde123456", "abcde123456", null, null));

        UserDTO updatedUser = new UserDTO(null, "abc12345", null, null);

        ObjectMapper objectMapper = new ObjectMapper();
        String updatedUserJson = objectMapper.writeValueAsString(updatedUser);

        MvcResult result = mockMvc.perform(put("/szs/modify-user")
                        .header("Authorization", "Bearer " + mockJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용자 정보가 성공적으로 업데이트되었습니다."))
                .andDo(document("{class-name}/{method-name}",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                        )
                ))
                .andReturn();

        String updateUserDetailsResponse = result.getResponse().getContentAsString();

        assertThat(JsonPath.<Boolean>read(updateUserDetailsResponse, "$.success")).isEqualTo(true);
    }

    @Test
    @DisplayName("DELETE /szs/delete-user")
    void deleteUser() throws Exception {
        String mockJwtToken = jwtTokenUtil.generateToken(new UserDTO("abcde123456", "abcde123456", null, null));

        MvcResult result = mockMvc.perform(delete("/szs/delete-user")
                        .header("Authorization", "Bearer " + mockJwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("사용자가 성공적으로 삭제되었습니다."))
                .andDo(document("{class-name}/{method-name}",
                        ApiDocumentUtils.getDocumentRequest(),
                        ApiDocumentUtils.getDocumentResponse(),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
                        )
                ))
                .andReturn();

        String deleteUserResponse = result.getResponse().getContentAsString();

        assertThat(JsonPath.<Boolean>read(deleteUserResponse, "$.success")).isEqualTo(true);
    }
}