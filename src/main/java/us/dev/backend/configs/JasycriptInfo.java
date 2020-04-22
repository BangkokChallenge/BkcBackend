package us.dev.backend.configs;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jasycript")
@NoArgsConstructor
@Getter @Setter
public class JasycriptInfo {

    @Value("${jasycript.db-path}")
    private String dbPath;

    @Value("${jasycript.db-user}")
    private String dbUser;

    @Value("${jasycript.db-pwd}")
    private String dbPwd;



}
