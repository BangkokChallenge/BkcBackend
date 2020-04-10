package us.dev.backend.Account;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import us.dev.backend.common.AppProperties;
import us.dev.backend.common.BaseControllerTest;
import us.dev.backend.common.TestDescription;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

public class AccountControllerTest extends BaseControllerTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AppProperties appProperties;


    @Test
    @TestDescription("카카오/자체Oauth 로그인테스트")
    public void createAccount() throws Exception {

        //given
        AccountDto accountDto = AccountDto.builder()
                .id("TEST_ID")
                .kakaoAccessToken("TEST_KAT")
                .kakaoRefreshToken("TEST_KRT")
                .username("TEST_UN")
                .profile_photo("TEST_PP")
                .nickname("TEST_NN")
                .build();

        //when&then
                mockMvc.perform(post("/api/account/login")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(accountDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andDo(document("createAccount",
                        links(
                                linkWithRel("self").description("현재 링크"),
                                linkWithRel("profile").description("도큐먼트 링크")
                        ),
                        requestFields(
                                fieldWithPath("id").description("고유 식별 카카오 ID"),
                                fieldWithPath("fcmToken").description("FCM TOKEN ID"),
                                fieldWithPath("kakaoAccessToken").description("카카오 AccessToken"),
                                fieldWithPath("kakaoRefreshToken").description("카카오 RefreshToken"),
                                fieldWithPath("serviceAccessToken").description("자체 AccessToken"),
                                fieldWithPath("serviceRefreshToken").description("자체 RefreshToken"),
                                fieldWithPath("password").description("값 조합을 위한 Password"),
                                fieldWithPath("profile_photo").description("회원 프로필 사진"),
                                fieldWithPath("nickname").description("Kakao 닉네임"),
                                fieldWithPath("username").description("회원 이름"),
                                fieldWithPath("roles").description("회원 권한")

                        ),
                        relaxedResponseFields(
                                fieldWithPath("qrid").description("회원 고유 QRCode"),
                                fieldWithPath("id").description("SNS로그인을 위한 Token id"),
                                fieldWithPath("fcmToken").description("Fcm Token id"),
                                fieldWithPath("kakaoAccessToken").description("KaKao AccessToken"),
                                fieldWithPath("kakaoRefreshToken").description("Kakao RefreshToken"),
                                fieldWithPath("serviceAccessToken").description("Service AccessToken"),
                                fieldWithPath("serviceRefreshToken").description("Service RefreshToken"),
                                fieldWithPath("password").description("회원 password"),
                                fieldWithPath("profile_photo").description("회원 프로필 사진진"),
                                fieldWithPath("nickname").description("Kakao 닉네"),
                                fieldWithPath("username").description("회원 이"),
                                fieldWithPath("roles").description("회원 권한")
                        )
                ))
        ;
    }


}