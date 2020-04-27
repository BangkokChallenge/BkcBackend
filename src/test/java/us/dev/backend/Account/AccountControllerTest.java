package us.dev.backend.Account;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import us.dev.backend.common.BaseControllerTest;
import us.dev.backend.common.TestDescription;
import us.dev.backend.configs.AppConfig;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

public class AccountControllerTest extends BaseControllerTest{
    /*
        이부분을 내장톰켓으로 진행해야하는 이유 :
        MockBean으로 등록하여 테스트를 진행하면 127.0.0.1/oauth/token 으로 진입불가.
        가 아니고 서버켜놓고 테스트 돌리면된다 ^^ㅣ바려나  하 ㅈ 같네
     */

    @Autowired
    AccountService accountService;

    @Autowired
    AppConfig appConfig;


    @Test
    @TestDescription("카카오/자체Oauth 로그인테스트")
    public void createAccount() throws Exception {
        //given
        AccountDtoKey accountDtoKey = AccountDtoKey.builder()
                .key("Bs0WZW_TdjWQ5FV5T8FF8J_JqX4Ak2Dgmnu3Jgo9dVwAAAFxu5lOxg")
                .build();

        //when&then
                mockMvc.perform(post("/api/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDtoKey)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andDo(document("createAccount",
                        links(
                                linkWithRel("self").description("현재 링크"),
                                linkWithRel("account").description("Account 링크"),
                                linkWithRel("profile").description("도큐먼트 링크")
                        ),
                        relaxedRequestFields(
                                fieldWithPath("key").description("KaKao Access Key")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("Kakao 고유 아이디"),
                                fieldWithPath("serviceAccessToken").description("자체 Service Access Token"),
                                fieldWithPath("serviceRefreshToken").description("자체 Service Refresh Token"),
                                fieldWithPath("password").description("내부 Security Value, 고정값:1234"),
                                fieldWithPath("profile_photo").description("KaKao 회원 프로필 사진"),
                                fieldWithPath("nickname").description("Kakao 닉네임"),
                                fieldWithPath("roles").description("회원 권한"),
                                fieldWithPath("createdAt").description("가입 시간")
                        )
                ))
        ;
    }


    @Test
    @TestDescription("회원정보가져오기 테스트")
    public void getAccount() throws Exception {
        //given
        this.mockMvc.perform(get("/api/account/{id}","TDD_TEMP_ID")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andDo(document("getAccount",
                        links(
                                linkWithRel("self").description("현재 링크"),
                                linkWithRel("profile").description("도큐먼트 링크")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("Kakao 고유 아이디"),
                                fieldWithPath("serviceAccessToken").description("자체 Service Access Token"),
                                fieldWithPath("serviceRefreshToken").description("자체 Service Refresh Token"),
                                fieldWithPath("password").description("내부 Security Value, 고정값:1234"),
                                fieldWithPath("profile_photo").description("KaKao 회원 프로필 사진"),
                                fieldWithPath("nickname").description("Kakao 닉네임"),
                                fieldWithPath("roles").description("회원 권한"),
                                fieldWithPath("createdAt").description("가입 시간")
                        ))
                )
        ;
    }


}