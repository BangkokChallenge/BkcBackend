package us.dev.backend.Post;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import us.dev.backend.Account.AccountService;
import us.dev.backend.common.AppProperties;
import us.dev.backend.common.BaseControllerTest;
import us.dev.backend.common.TestDescription;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostControllerTest extends BaseControllerTest {

    @Autowired
    AppProperties appProperties;

    @Autowired
    AccountService accountService;



    @Test
    @TestDescription("Post upload 테스트")
    public void uploadPost() throws Exception {
        //given


        /* Form data */
        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());

        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();

        map.add("hashTag","#홍제#짱짱 #짱짱맨");
        map.add("article","테스트내용");


        //when&then
        mockMvc.perform(multipart("/api/post/upload").file(file).params(map)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("file").exists())
                .andExpect(jsonPath("article").exists())
                .andDo(document("uploadPost",
                        links(
                                linkWithRel("self").description("현재 링크"),
                                linkWithRel("post list").description("Post List를 가져오는 링크"),
                                linkWithRel("profile").description("도큐먼트 링크")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("createdAt").description("Post 생성 날짜"),
                                fieldWithPath("modifiedAt").description("Post 수정 날짜"),
                                fieldWithPath("accountId").description("사용자 id"),
                                fieldWithPath("id").description("Post id"),
                                fieldWithPath("article").description("Post 작성글"),
                                fieldWithPath("hashTag").description("Hash Tag"),
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
                .andExpect(status().isCreated())
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
                                fieldWithPath("_embedded").description("Post 페이지정보들[기본리턴속성임]"),
                                fieldWithPath("_embedded.postList[0].accountId").description("작성자 Kakao ID"),
                                fieldWithPath("_embedded.postList[0].nickname").description("작성자 Kakao Nickname"),
                                fieldWithPath("_embedded.postList[0].profile_photo").description("작성자 KaKao Profile Photo"),
                                fieldWithPath("_embedded.postList[0].id").description("Post ID"),
                                fieldWithPath("_embedded.postList[0].hashTag").description("Post HashTag"),
                                fieldWithPath("_embedded.postList[0].selfLike").description("작성자가 해당 Post에 좋아요를 눌렀는지 여부"),
                                fieldWithPath("_embedded.postList[0].commentCount").description("댓글 수 "),
                                fieldWithPath("_embedded.postList[0].likeCount").description("좋아요 수 "),
                                fieldWithPath("_embedded.postList[0].article").description("Post 내용"),
                                fieldWithPath("_embedded.postList[0].filePath").description("S3 image file path"),
                                fieldWithPath("page").description("Paging 정보들")

                        )
                ))
        ;
    }

}