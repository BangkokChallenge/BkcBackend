package us.dev.backend.configs;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.filters.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import us.dev.backend.Account.AccountService;
import us.dev.backend.common.AppProperties;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    /*
        OAuth2 권한 서버 설정.
        @EnableAuthorizationServer : OAuth2 인증 서버를 활성화시켜주는 Annotation.
        OAuth2 인증을 위한 AccessToken, RefreshToken을 통한 OAuth2 인증 등 핵심기능을 활성화시켜줌.
        /oauth/token
        /oauth/authorize
        보통 인증서버는 따로두어 관리한다.
            1. 서버 하나가 중지되더라도 운용하기위함.
            2. 인증서버와 API 서버가 같이존재하기 때문에 API서버를 추가하거나 변경할 때 인증서버도 같이 종료되는 문제가 있음.
     */
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AccountService accountService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private AppProperties appProperties;

    /* OAuth2 인증서버 자체의 보안을 설정하는 부분 -> PasswordEncoder를 통한 암호화. */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder)
            .tokenKeyAccess("permitAll()")
            .checkTokenAccess("permitAll()");
        // Token 정보를 API(/oauth/check_token)를 활성화 시킨다. ( 기본은 denyAll )

        security.allowFormAuthenticationForClients();
    }

    /*
        Client 설정. 기기 단의 정보.
        Oauth/token 으로 접속시 Body에 해당 내용을 보냄 -> 올바르다면 Token을 발급함.
        Client들도 관리하고 싶다면 clients.jdbc(datasource)를 사용하고 테이블 맵핑해주어야함.
    */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //앱에서 해줘야하는 부분
                clients.inMemory()
                        .withClient(appProperties.getClientId())
                        .authorizedGrantTypes("password","refresh_token")
                        .scopes("read","write")
                        .secret(this.passwordEncoder.encode(appProperties.getClientSecret()))
                        .accessTokenValiditySeconds(24 * 60 * 60 * 7)
                        .refreshTokenValiditySeconds(24 * 60 * 60 * 7 * 2);
    }

    /* Oauth2서버가 작동하기 위한 EndPoint에 대한 정보를 설정 */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(accountService)
                .tokenStore(tokenStore);
    }




    @Primary
    @Bean
    public RemoteTokenServices tokenService() {
        RemoteTokenServices tokenService = new RemoteTokenServices();
        tokenService.setCheckTokenEndpointUrl(
                "http://localhost:8080/oauth/check_token");
        tokenService.setClientId(appProperties.getClientId());
        tokenService.setClientSecret(appProperties.getClientSecret());
        return tokenService;
    }

}
