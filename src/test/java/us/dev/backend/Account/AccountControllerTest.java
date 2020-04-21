package us.dev.backend.Account;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import us.dev.backend.common.AppProperties;
import us.dev.backend.common.BaseControllerTest;
import us.dev.backend.common.TestDescription;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
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

        //when&then
                mockMvc.perform(get("/api/account/login/{key}","dMiixCphdLu5W3_xbLdzwqaLwEGr-TiSTUYMMQo9dRsAAAFxnUcs7g"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andDo(document("createAccount",
                        links(
                                linkWithRel("self").description("현재 링크"),
                                linkWithRel("Account").description("Account 링크"),
                                linkWithRel("profile").description("도큐먼트 링크")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("Kakao 고유 아이디"),
                                fieldWithPath("fcmToken").description("kakao FCM Token"),
                                fieldWithPath("serviceAccessToken").description("자체 Service Access Token"),
                                fieldWithPath("serviceRefreshToken").description("자체 Service Refresh Token"),
                                fieldWithPath("password").description("내부 Security Value, 고정값:1234"),
                                fieldWithPath("profile_photo").description("KaKao 회원 프로필 사진"),
                                fieldWithPath("nickname").description("Kakao 닉네임"),
                                fieldWithPath("roles").description("회원 권한")
                        )
                ))
        ;
    }


}