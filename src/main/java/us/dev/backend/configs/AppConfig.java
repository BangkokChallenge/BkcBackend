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

                    Post initPost = Post.builder()
                            .accountId("Kakao_FIX_ID")
                            .article("미안하다 이거 보여주려고 어그로끌었다.. 나루토 사스케 싸움수준 ㄹㅇ실화냐? 진짜 세계관최강자들의 싸움이다.. 그찐따같던 나루토가 맞나? 진짜 나루토는 전설이다..진짜옛날에 맨날나루토봘는데 왕같은존재인 호카게 되서 세계최강 전설적인 영웅이된나루토보면 진짜내가다 감격스럽고 나루토 노래부터 명장면까지 가슴울리는장면들이 뇌리에 스치면서 가슴이 웅장해진다.. 그리고 극장판 에 카카시앞에 운석날라오는 거대한 걸 사스케가 갑자기 순식간에 나타나서 부숴버리곤 개간지나게 나루토가 없다면 마을을 지킬 자는 나밖에 없다 라며 바람처럼 사라진장면은 진짜 나루토처음부터 본사람이면 안울수가없더라 진짜 너무 감격스럽고 보루토를 최근에 알았는데 미안하다.. 지금20화보는데 진짜 나루토세대나와서 너무 감격스럽고 모두어엿하게 큰거보니 내가 다 뭔가 알수없는 추억이라해야되나 그런감정이 이상하게 얽혀있다.. 시노는 말이많아진거같다 좋은선생이고..그리고 보루토왜욕하냐 귀여운데 나루토를보는것같다 성격도 닮았어 그리고버루토에 나루토사스케 둘이싸워도 이기는 신같은존재 나온다는게 사실임?? 그리고인터닛에 쳐봣는디 이거 ㄹㅇㄹㅇ 진짜팩트냐?? 저적이 보루토에 나오는 신급괴물임?ㅡ 나루토사스케 합체한거봐라 진짜 ㅆㅂ 이거보고 개충격먹어가지고 와 소리 저절로 나오더라 ;; 진짜 저건 개오지는데.. 저게 ㄹㅇ이면 진짜 꼭봐야돼 진짜 세계도 파괴시키는거아니야 .. 와 진짜 나루토사스케가 저렇게 되다니 진짜 눈물나려고했다.. 버루토그라서 계속보는중인데 저거 ㄹㅇ이냐..? 하.. ㅆㅂ 사스케 보고싶다..  진짜언제 이렇게 신급 최강들이 되었을까 옛날생각나고 나 중딩때생각나고 뭔가 슬프기도하고 좋기도하고 감격도하고 여러가지감정이 복잡하네.. 아무튼 나루토는 진짜 애니중최거명작임..")
                            .filePath("https://jayass3cloud.s3.ap-northeast-2.amazonaws.com/IMG_8456.jpg")
                            .profile_photo("http://k.kakaocdn.net/dn/oMYoX/btqDheA1EpU/CiRZnaTetvs2OfkeRcTQL0/img_640x640.jpg")
                            .nickname("Kakao Nickname")
                            .hashTag("미안하다 이거보여주려고 어그로 끌었다.")
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
