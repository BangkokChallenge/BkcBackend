package us.dev.backend.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "membership-app")
@Getter
@Setter
public class AppProperties {
    /*
        String field <-> application.properties 데이터
     */

    @NotEmpty
    @Value("${membership-app.client-id}")
    private String clientId;

    @NotEmpty
    @Value("${membership-app.client-secret}")
    private String clientSecret;

    @NotEmpty
    @Value("${membership-app.get-oauth-u-r-l}")
    private String getOauthURL;

}
