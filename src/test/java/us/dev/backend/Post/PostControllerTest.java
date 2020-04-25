package us.dev.backend.Post;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountRole;
import us.dev.backend.Account.AccountService;
import us.dev.backend.common.AppProperties;
import us.dev.backend.common.BaseControllerTest;
import us.dev.backend.common.TestDescription;

import java.util.Set;

import static org.junit.Assert.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostControllerTest extends BaseControllerTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    AccountService accountService;

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

    @Test
    @TestDescription("Post upload 테스트")
    public void uploadPost() throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        PostDto postDto = PostDto.builder()
                .id(11)
                .article("TEST")
                .build();

        //TODO 파일업로드 테스트 작성해야함.
        //나눠야할 수도 있음. controller 부분.
        //when&then
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/post/upload").file(file)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andDo(document("uploadPost",
                        links(
                                linkWithRel("self").description("도큐먼트 링크"),
                                linkWithRel("profile").description("도큐먼트 링크")
                        ),
                        relaxedRequestFields(
                                fieldWithPath("article").description("Name of new event")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("createdAt").description("Post 생성 날짜"),
                                fieldWithPath("modifiedAt").description("Post 수정 날짜"),
                                fieldWithPath("userId").description("사용자 id"),
                                fieldWithPath("id").description("Post id"),
                                fieldWithPath("hashTag").description("Hash Tage"),
                                fieldWithPath("filePath").description("file path"),
                                fieldWithPath("selfLike").description("좋아요 여부"),
                                fieldWithPath("commentCount").description("댓글 수"),
                                fieldWithPath("likeCount").description("좋아요 수")
                        )
                ))
        ;
    }


    @Test
    @TestDescription("Page List를 불러오는 테스트")
    public void getPosts() throws Exception {
        //given
        this.mockMvc.perform(get("/api/post/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("getPosts",links(
                        linkWithRel("self").description("현재 링크"),
                        linkWithRel("profile").description("도큐먼트 링크"),
                        linkWithRel("next").description("다음 페이지 링크"),
                        linkWithRel("first").description("첫 페이지 링크"),
                        linkWithRel("last").description("마지막 페이지 링크")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded").description("Post 페이지정보들")
                        )
                        ))
        ;
    }

    private String getBearerToken() throws Exception {
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


}