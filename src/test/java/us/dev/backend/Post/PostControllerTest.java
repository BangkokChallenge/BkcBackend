package us.dev.backend.Post;

import org.junit.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import us.dev.backend.common.BaseControllerTest;
import us.dev.backend.common.TestDescription;

import static org.junit.Assert.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostControllerTest extends BaseControllerTest {

    @Test
    @TestDescription("Post upload 테스트")
    public void uploadPost() throws Exception {
        //given

        MockMultipartFile file = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "Hello, World!".getBytes());
        //TODO 파일업로드 테스트 작성해야함.
        //나눠야할 수도 있음. controller 부분.
        //when&then
        mockMvc.perform(post("/api/post/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content(objectMapper.writeValueAsString(Post)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andDo(document("createAccount",
                        links(
                                linkWithRel("self").description("현재 링크"),
                                linkWithRel("Account").description("Account 링크"),
                                linkWithRel("profile").description("도큐먼트 링크")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new event"),
                                fieldWithPath("description").description("Description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time of beginEnrollment"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time of closeEnrollmentDateTime"),
                                fieldWithPath("beginEventDateTime").description("date time of begin")
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


    @Test
    @TestDescription("Page List를 불러오는 테스트")
    public void getPostList() throws Exception {
        //given
        this.mockMvc.perform(get("/api/post/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-posts"))
        ;
    }


}