package us.dev.backend.Like;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import us.dev.backend.common.BaseControllerTest;
import us.dev.backend.common.TestDescription;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

public class LikeControllerTest extends BaseControllerTest {


    @Test
    @TestDescription("좋아요 눌렀을 때 반영테스트")
    public void likeChangeState() throws Exception{

       this.mockMvc.perform(put("/api/like//{postId}","1")
               .header(HttpHeaders.AUTHORIZATION, getBearerToken()))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(jsonPath("postId").exists())
               .andDo(document("likeChangeState",
                       relaxedResponseFields(
                               fieldWithPath("id").description("LikePost PK"),
                               fieldWithPath("accountId").description("좋아요를 누른 사용자 카카오 아이디"),
                               fieldWithPath("postId").description("좋아요가 반영된 Post 아이디"),
                               fieldWithPath("likeTrueAndFalse").description("변경된 좋아요 상태")
                       ))
       );
    }

}