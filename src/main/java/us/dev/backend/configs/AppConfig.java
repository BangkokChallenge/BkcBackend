package us.dev.backend.configs;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountRepository;
import us.dev.backend.Account.AccountRole;
import us.dev.backend.Account.AccountService;
import us.dev.backend.HashTag.HashTag;
import us.dev.backend.Like.LikePost;
import us.dev.backend.Like.LikeRepository;
import us.dev.backend.Post.Post;
import us.dev.backend.Post.PostRepository;
import us.dev.backend.common.AppProperties;

import java.util.*;
import java.util.stream.IntStream;

@Configuration
public class AppConfig {

    @Autowired
    AppProperties appProperties;

    /* 데이터 Migration을 위한 modelmapper[이름이 같으면 옮겨짐] */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate customizeRestTemplate() {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(10000);
        requestFactory.setConnectTimeout(10000);
        requestFactory.setOutputStreaming(false);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        messageConverters.add(new FormHttpMessageConverter());
        messageConverters.add(new StringHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    /* Password암호화 Encoder */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    /* TEST를 위한 유저 생성 */
    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;

            @Autowired
            PostRepository postRepository;

            @Autowired
            AccountRepository accountRepository;

            @Autowired
            LikeRepository likeRepository;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                Account account = Account.builder()
                        .id("TDD_TEMP_ID")
                        .password("1234")
                        .nickname("TDD_NICKNAME")
                        .roles(Set.of(AccountRole.USER))
                        .profile_photo("TDD_PHOTO")
                        .build();
                accountService.saveAccount(account);


                /* 시작하자마자 Oauth Token 받기 (테스트용) */
                HttpHeaders headers = new HttpHeaders();
                headers.setBasicAuth(appProperties.getClientId(),appProperties.getClientSecret());
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

                MultiValueMap<String,String> parameters = new LinkedMultiValueMap<>();
                parameters.add("grant_type","password");
                parameters.add("username","TDD_TEMP_ID");
                parameters.add("password","1234");
                HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<>(parameters,headers);

                Jackson2JsonParser parser2 = new Jackson2JsonParser();

                RestTemplate restTemplate = new RestTemplate();
                String response = restTemplate.postForObject(appProperties.getGetOauthURL(),requestEntity,String.class);

                String getaccess_Token = parser2.parseMap(response).get("access_token").toString();
                String getrefrsh_Token = parser2.parseMap(response).get("refresh_token").toString();

                System.out.println("***ACCESS_TOKEN***");
                System.out.println(getaccess_Token);
                System.out.println("***REFRESH_TOKEN***");
                System.out.println(getrefrsh_Token);

                Optional<Account> getOptional = accountRepository.findById("TDD_TEMP_ID");
                Account newAccount = getOptional.get();
                newAccount.setServiceAccessToken(getaccess_Token);
                newAccount.setServiceRefreshToken(getrefrsh_Token);
                accountService.saveAccount(newAccount);




                /* test data 여러개 집어넣기 */
                IntStream.rangeClosed(1, 40).forEach(index -> {
                    List<HashTag> hashTags = new ArrayList<>();

                    // .hashTag에 init 정보 넣어주셈.

                    Post initPost = Post.builder()
                            .accountId("Kakao_FIX_ID")
                            .article("미안하다 이거 보여주려고 어그로끌었다.. 나루토 사스케 싸움수준 ㄹㅇ실화냐? 진짜 세계관최강자들의 싸움이다.. 그찐따같던 나루토가 맞나? 진짜 나루토는 전설이다.")
                            .filePath("https://jayass3cloud.s3.ap-northeast-2.amazonaws.com/IMG_8456.jpg")
                            .profile_photo("http://k.kakaocdn.net/dn/oMYoX/btqDheA1EpU/CiRZnaTetvs2OfkeRcTQL0/img_640x640.jpg")
                            .nickname("Kakao Nickname")
                            //.hashTag({"a","b"})
                            .build();

                    Post newPost = postRepository.save(initPost);
                    LikePost initLike = LikePost.builder()
                            .accountId(newPost.getAccountId())
                            .likeTrueAndFalse(false)
                            .postId(newPost.getId())
                            .build();

                    likeRepository.save(initLike);


                });
            }
        };
    }

}
