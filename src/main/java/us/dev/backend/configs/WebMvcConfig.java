package us.dev.backend.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    /* 이거해줘야 thymeleaf 에서 Bootstrap resource 긁을 수 있음. */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/vendor/**",
                "/img/**",
                "/css/**",
                "/js/**",
                "/scss/**",
                "/docs/**")
                .addResourceLocations(
                        "classpath:/static/vendor/",
                        "classpath:/static/img/",
                        "classpath:/static/css/",
                        "classpath:/static/js/",
                        "classpath:/static/scss/",
                        "classpath:/static/docs/");
    }
}
