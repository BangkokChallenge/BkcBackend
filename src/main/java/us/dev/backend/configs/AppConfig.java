package us.dev.backend.configs;

import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Lists;
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
import us.dev.backend.Account.AccountRole;
import us.dev.backend.Account.AccountService;
import us.dev.backend.Post.Post;
import us.dev.backend.Post.PostRepository;
import us.dev.backend.common.AppProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
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

                /* test data 여러개 집어넣기 */
                IntStream.rangeClosed(1, 40).forEach(index ->
                        postRepository.save(Post.builder()
                                .id(11)
                                .userId("TDD_USER_ID")
                                .article("작성내용")
                                .filePath("S3 image 주소")
                                .profile_photo("Kakao profile photo")
                                .nickname("Kakao Nickname")
                                .hashTag("일상")
                                .build()));


            }
        };
    }

}
