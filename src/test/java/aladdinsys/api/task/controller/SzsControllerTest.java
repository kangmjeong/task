package aladdinsys.api.task.controller;

import aladdinsys.api.task.ApiDocumentUtils;
import aladdinsys.api.task.dto.UserDTO;
import aladdinsys.api.task.service.UserService;
import aladdinsys.api.task.utils.jwt.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
class SzsControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService; // 실제 서비스 사용

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    void signUp() throws Exception {
        UserDTO validUser = new UserDTO("abcde1234567", "abcde1234567", "홍길동", "860824-1655068");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(validUser);
        // MockMvc를 사용하여 HTTP 요청 수행 및 응답 검증
        ResultActions resultActions = mockMvc.perform(post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("저장이 완료되었습니다.")); // 이 줄의 끝에 불필요한 점(.) 제거

        // 응답 문자열 추출
        String jsonResponse = resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        // JSON 문자열을 예상한 결과와 비교
        String expectedJsonResponse = "{\"success\": true, \"message\": \"저장이 완료되었습니다.\"}";
        Assertions.assertEquals(expectedJsonResponse, jsonResponse);
    }
    }

//    @Test
//    void login() throws Exception {
//        JsonObject mockResponse = new JsonObject();
//        mockResponse.addProperty("success", true);
//        mockResponse.addProperty("message", "로그인 성공");
//        mockResponse.addProperty("token", "someGeneratedToken");
//        when(userService.login(any(UserDTO.class))).thenReturn(mockResponse.toString());
//
//        String userJson = "{\"userId\":\"abcde123456\",\"password\":\"abcde123456\"}";
//
//        mockMvc.perform(post("/szs/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(userJson))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("로그인 성공"))
//                .andExpect(jsonPath("$.token").exists())
//
//                .andDo(document("{class-name}/{method-name}",
//                        ApiDocumentUtils.getDocumentRequest(),
//                        ApiDocumentUtils.getDocumentResponse(),
//                        responseFields(
//                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
//                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
//                                fieldWithPath("token").type(JsonFieldType.STRING).description("JWT 토큰")
//                        )
//                ));
//    }

//    @Test
//    void userDetail() throws Exception {
//        when(jwtTokenUtil.validateToken(anyString())).thenReturn(true);
//        when(jwtTokenUtil.getUserIdFromToken(anyString())).thenReturn("abc123456");
//
//        JsonObject mockResponse = new JsonObject();
//        mockResponse.addProperty("success", true);
//
//        JsonObject userObject = new JsonObject();
//        userObject.addProperty("userId", "abc123456");
//        userObject.addProperty("password", "encryptedPassword");
//        userObject.addProperty("name", "홍길동");
//        userObject.addProperty("regNo", "encryptedRegNo");
//
//        mockResponse.add("user", userObject);
//
//        when(userService.userDetail("abc123456")).thenReturn(mockResponse.toString());
//
//        String mockJwtToken = "someGeneratedToken";
//        Principal mockPrincipal = Mockito.mock(Principal.class);
//        Mockito.when(mockPrincipal.getName()).thenReturn("abc123456");
//
//        mockMvc.perform(get("/szs/user-detail")
//                        .header("Authorization", "Bearer " + mockJwtToken)
//                        .principal(mockPrincipal))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.user.userId").value("abc123456"))
//                .andDo(document("{class-name}/{method-name}",
//                        ApiDocumentUtils.getDocumentRequest(),
//                        ApiDocumentUtils.getDocumentResponse(),
//                        responseFields(
//                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
//                                fieldWithPath("user.userId").type(JsonFieldType.STRING).description("사용자 ID"),
//                                fieldWithPath("user.password").type(JsonFieldType.STRING).description("사용자 비밀번호"),
//                                fieldWithPath("user.name").type(JsonFieldType.STRING).description("사용자 이름"),
//                                fieldWithPath("user.regNo").type(JsonFieldType.STRING).description("사용자 등록번호")
//                        )
//                ));
//    }
//
//    @Test
//    void updateUserDetails() throws Exception {
//
//        when(jwtTokenUtil.validateToken(anyString())).thenReturn(true);
//        when(jwtTokenUtil.getUserIdFromToken(anyString())).thenReturn("abc123456");
//
//        JsonObject mockResponse = new JsonObject();
//        mockResponse.addProperty("success", true);
//        mockResponse.addProperty("message", "사용자 정보가 성공적으로 업데이트되었습니다.");
//        when(userService.updateUserDetails(eq("abc123456"), any(UserDTO.class)))
//                .thenReturn(mockResponse.toString());
//
//        String mockJwtToken = "someGeneratedToken";
//        Principal mockPrincipal = Mockito.mock(Principal.class);
//        Mockito.when(mockPrincipal.getName()).thenReturn("abc123456");
//
//        UserDTO updatedUser = new UserDTO(null, "newPassword", null, null);
//
//        mockMvc.perform(put("/szs/modify-user")
//                        .header("Authorization", "Bearer " + mockJwtToken)
//                        .principal(mockPrincipal)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new Gson().toJson(updatedUser)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("사용자 정보가 성공적으로 업데이트되었습니다."))
//                .andDo(document("{class-name}/{method-name}",
//                        ApiDocumentUtils.getDocumentRequest(),
//                        ApiDocumentUtils.getDocumentResponse(),
//                        responseFields(
//                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
//                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
//                        )
//                ));
//    }
//
//    @Test
//    void deleteUser() throws Exception {
//        when(jwtTokenUtil.validateToken(anyString())).thenReturn(true);
//        when(jwtTokenUtil.getUserIdFromToken(anyString())).thenReturn("abc123456");
//
//        JsonObject mockResponse = new JsonObject();
//        mockResponse.addProperty("success", true);
//        mockResponse.addProperty("message", "사용자가 성공적으로 삭제되었습니다.");
//        when(userService.deleteUser("abc123456")).thenReturn(mockResponse.toString());
//
//        String mockJwtToken = "someGeneratedToken";
//        Principal mockPrincipal = Mockito.mock(Principal.class);
//        Mockito.when(mockPrincipal.getName()).thenReturn("abc123456");
//
//        mockMvc.perform(delete("/szs/delete-user")
//                        .header("Authorization", "Bearer " + mockJwtToken)
//                        .principal(mockPrincipal))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.message").value("사용자가 성공적으로 삭제되었습니다."))
//                .andDo(document("{class-name}/{method-name}",
//                        ApiDocumentUtils.getDocumentRequest(),
//                        ApiDocumentUtils.getDocumentResponse(),
//                        responseFields(
//                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
//                                fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지")
//                        )
//                ));
//    }
