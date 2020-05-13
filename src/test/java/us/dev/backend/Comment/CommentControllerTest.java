package us.dev.backend.Comment;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountRepository;
import us.dev.backend.Account.AccountRole;
import us.dev.backend.Post.Post;
import us.dev.backend.Post.PostRepository;
import us.dev.backend.common.AppProperties;
import us.dev.backend.common.BaseControllerTest;
import us.dev.backend.common.TestDescription;

import java.util.Set;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class CommentControllerTest extends BaseControllerTest {
    @Autowired
    AppProperties appProperties;

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;

    @Test
    @TestDescription("Comment List를 불러오는 테스트")
    public void getComments() throws Exception {
        //given
        Account account = Account.builder()
                .id("TEST_ID")
                .password("TEST_PWD")
                .nickname("TEST_NICKNAME")
                .profile_photo("TEST_PROFILEPT")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountRepository.save(account);
        Post post = Post.builder()
                .id(11)
                .accountId("TDD_USER_ID")
                .article("작성내용")
                .filePath("S3 image 주소")
                .profile_photo("Kakao profile photo")
                .nickname("Kakao Nickname")
//                .hashTag("일상")
                .build();
        postRepository.save(post);
        Comment comment = Comment.builder()
                .content("test")
                .post(post)
                .account(account)
                .build();
        commentRepository.save(comment);

        this.mockMvc.perform(RestDocumentationRequestBuilders.get("/api/post/{id}/comment", 11)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
                .andExpect(status().isOk())
                .andDo(document("getComments",
                        pathParameters(
                                parameterWithName("id").description("post 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("[0].id").description("comment 아이디"),
                                fieldWithPath("[0].content").description("comment 내용"),
                                fieldWithPath("[0].createdAt").description("comment 작성시간"),
                                fieldWithPath("[0].modifiedAt").description("comment 수정시간"),
                                fieldWithPath("[0].postId").description("Post 아이디"),
                                fieldWithPath("[0].accountId").description("작성자 Kakao ID"),
                                fieldWithPath("[0].profile_photo").description("작성자 Kakao Profile Photo"),
                                fieldWithPath("[0].nickname").description("작성자 Kakao Nickname")
                        )
                        )
                );
    }

    @Test
    @TestDescription("Comment 등록 테스트")
    public void postComments() throws Exception {
        //given
        CommentDto commentDto = new CommentDto();
        commentDto.setContent("test");
        this.mockMvc.perform(RestDocumentationRequestBuilders.post("/api/post/{id}/comment", 11)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("postComments",
                        pathParameters(
                                parameterWithName("id").description("post 아이디")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("comment 아이디"),
                                fieldWithPath("content").description("comment 내용"),
                                fieldWithPath("createdAt").description("comment 작성시간"),
                                fieldWithPath("modifiedAt").description("comment 수정시간"),
                                fieldWithPath("postId").description("Post 아이디"),
                                fieldWithPath("accountId").description("작성자 Kakao ID"),
                                fieldWithPath("profile_photo").description("작성자 Kakao Profile Photo"),
                                fieldWithPath("nickname").description("작성자 Kakao Nickname")
                        )
                        )
                );
    }

//    @Test
//    @TestDescription("Comment 업데이트 테스트")
//    public void updateComments() throws Exception {
//        //given
//        CommentDto commentDto = new CommentDto();
//        commentDto.setContent("updatedTest");
//
//        this.mockMvc.perform(RestDocumentationRequestBuilders.put("/api/post/{postId}/comment/{commentId}", 11, 1)
//                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(commentDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andDo(document("updateComments",
//                        pathParameters(
//                                parameterWithName("postId").description("post 아이디"),
//                                parameterWithName("commentId").description("comment 아이디")
//                        ),
//                        relaxedResponseFields(
//                                fieldWithPath("id").description("comment 아이디"),
//                                fieldWithPath("content").description("comment 내용"),
//                                fieldWithPath("createdAt").description("comment 작성시간"),
//                                fieldWithPath("modifiedAt").description("comment 수정시간"),
//                                fieldWithPath("postId").description("Post 아이디"),
//                                fieldWithPath("accountId").description("작성자 Kakao ID"),
//                                fieldWithPath("accountNickName").description("작성자 Kakao Nickname")
//                        )
//                        )
//                );
//    }
}