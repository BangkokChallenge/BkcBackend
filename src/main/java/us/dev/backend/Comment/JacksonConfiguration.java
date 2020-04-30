package us.dev.backend.Comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Comment.class, new CommentSerializer());
//        objectMapper.registerModule(simpleModule);
//
//        return objectMapper;
//    }
}