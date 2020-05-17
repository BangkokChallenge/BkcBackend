package us.dev.backend.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import us.dev.backend.Account.AccountRole;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    /*
        API 서버 권한 설정.
        접근 보호.
     */

    private static final String RESOURCE_ID = "resource_id";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(false);
    }


    //TODO 뷰와 Oauth2를 동시에 적용하는 것은 오바인것같다.
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .anonymous()
                .and()
                .authorizeRequests()
                    .antMatchers("/manager","/resources/**","/static/**","/templates/**").permitAll()
                    .antMatchers("/manager/**").permitAll()
                    .antMatchers("/oauth/token").permitAll()
                    .antMatchers("/oauth/check_token").permitAll()
                    .antMatchers("/api/account/login").permitAll()
                    .antMatchers("/api/account/refresh").permitAll()
                    .antMatchers("/api/account/checkToken").permitAll()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .anyRequest()
                    .authenticated()
                    //.permitAll()
                .and()
                .cors()
                .and()
                .formLogin()
                    .loginPage("/manager").permitAll()
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/manager/home",true)
                .and()
                .httpBasic()

                .and()
                .csrf().disable()
                .exceptionHandling()
                .accessDeniedHandler(new OAuth2AccessDeniedHandler());
    }
}
