package us.dev.backend.Account;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.tomcat.util.json.JSONParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.*;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import us.dev.backend.common.AppProperties;
import us.dev.backend.common.ErrorsResource;
import us.dev.backend.configs.AppConfig;
import us.dev.backend.configs.RestTemplateLoggingRequestInterceptor;

import javax.validation.Valid;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/account", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
@CrossOrigin("*")
public class AccountController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AppConfig appConfig;

    @Autowired
    AppProperties appProperties;


    /* 로그인으로 회원정보 생성하여 리턴해주기 */
    @PostMapping("/login")
    public ResponseEntity AccountLogin(@RequestBody AccountDtoKey accountDtoKey) {

        String key = accountDtoKey.getKey();

        RestTemplate restTemplate;
        HttpHeaders headers;

        JsonParser parser = new JsonParser();


        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+ key);
        headers.setContentType(MediaTypes.HAL_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);


        restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.GET,entity,String.class);
        restTemplate.setInterceptors(Arrays.asList(new RestTemplateLoggingRequestInterceptor()));

        JsonElement element = parser.parse(responseEntity.getBody());
        String kakaoId = element.getAsJsonObject().get("id").getAsString();
        JsonObject kakaoProperties = element.getAsJsonObject().get("properties").getAsJsonObject();

        String kakaoUsername = kakaoProperties.get("nickname").getAsString();
        String kakaoProfile_image = kakaoProperties.get("profile_image").getAsString();


        /* Dto -> 'UserInfo'로 변환 */
        Account account = Account.builder()
                .id(kakaoId)
                .password("1234")
                .roles(Set.of(AccountRole.USER))
                .nickname(kakaoUsername)
                .profile_photo(kakaoProfile_image)
                .build();

        /* 저장을 한번해야 Oauth2 인증 가능 */
        Account newAccount = this.accountService.saveAccount(account);


        /* 자체 Oauth2 인증 */
        headers = new HttpHeaders();
        headers.setBasicAuth(appProperties.getClientId(),appProperties.getClientSecret());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String,String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type","password");
        parameters.add("username",newAccount.getId());
        parameters.add("password","1234");
        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<>(parameters,headers);

        Jackson2JsonParser parser2 = new Jackson2JsonParser();

        //restTemplate = appConfig.customizeRestTemplate();
        restTemplate = new RestTemplate();
        String response = restTemplate.postForObject(appProperties.getGetOauthURL(),requestEntity,String.class);
        restTemplate.setInterceptors(Arrays.asList(new RestTemplateLoggingRequestInterceptor()));

        String getaccess_Token = parser2.parseMap(response).get("access_token").toString();
        String getrefrsh_Token = parser2.parseMap(response).get("refresh_token").toString();


        /* Token 제대로 읽었는지 확인 */
        if(getaccess_Token == null || getrefrsh_Token == null ) {
            accountRepository.delete(account);
            return ResponseEntity.notFound().build();
        }
        newAccount.setServiceAccessToken(getaccess_Token);
        newAccount.setServiceRefreshToken(getrefrsh_Token);


        /* 마지막으로 최종 저장  */
        this.accountRepository.save(newAccount);


        /* HATEOUS */
        ControllerLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash("/login/{key}");
        URI createdUri = selfLinkBuilder.toUri();

        AccountResource accountResource = new AccountResource(newAccount);
        accountResource.add(linkTo(AccountController.class).withRel("account"));

        accountResource.add(new Link("/docs/index.html#resource-createAccount").withRel("profile"));
        return ResponseEntity.created(createdUri).body(accountResource);

    }

    /* 회원정보 가져오기 */
    @GetMapping("/{id}")
    public ResponseEntity getAccountInfo(@PathVariable String id) throws Exception{
        Optional<Account> optionalAccount = this.accountRepository.findById(id);
        if(optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account account = optionalAccount.get();
        AccountResource accountResource = new AccountResource(account);
        accountResource.add(new Link("/docs/index.html#resource-getAccount").withRel("profile"));
        return ResponseEntity.ok(accountResource);
    }

    /* 회원정보 수정 -> 아직 필요 없음 */
    @PutMapping("/{id}")
    public ResponseEntity updateUserInfo(@PathVariable String id, @RequestBody @Valid AccountDto accountDto, Errors errors) {
        Optional<Account> optionalAccountDto = this.accountRepository.findById(id);
        if(optionalAccountDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        //데이터 바인딩 잘되었는지 확인.
        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        //여기서 비즈니스로직 추가하고 에러 확인 함 더 해도됨.

        Account existingAccount = optionalAccountDto.get();
        this.appConfig.modelMapper().map(accountDto,existingAccount);
        Account savedAccount = this.accountRepository.save(existingAccount);

        AccountResource accountResource = new AccountResource(savedAccount);
        accountResource.add(new Link("/docs/index.html#resource-updateUserInfo").withRel("profile"));

        return ResponseEntity.ok(accountResource);
    }


    /* BadRequest Customize */
    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }


}
