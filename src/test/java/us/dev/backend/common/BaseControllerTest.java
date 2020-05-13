package us.dev.backend.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountRole;
import us.dev.backend.Account.AccountService;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
// @WebMvcTest
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Ignore
public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

    @Autowired
    protected AppProperties appProperties;

    @Autowired
    protected AccountService accountService;

    @Before
    public void createTddAccount() {
        Account account = Account.builder()
                .id("TDD_TEMP_ID")
                .password("1234")
                .nickname("TDD_NICKNAME")
                .roles(Set.of(AccountRole.USER))
                .profile_photo("TDD_PHOTO")
                .build();

        accountService.saveAccount(account);
    }

    public String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }


    public String getAccessToken() throws Exception{
        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret())) //basic Auth 라는 header를 만듦.
                .param("username","TDD_TEMP_ID")
                .param("password","1234")
                .param("grant_type","password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists()
                );

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }

    public String getRefreshToken() throws Exception{
        ResultActions perform = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(),appProperties.getClientSecret())) //basic Auth 라는 header를 만듦.
                .param("username","TDD_TEMP_ID")
                .param("password","1234")
                .param("grant_type","password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("refresh_token").exists()
                );

        var responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("refresh_token").toString();
    }
}
