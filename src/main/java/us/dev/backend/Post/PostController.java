package us.dev.backend.Post;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import us.dev.backend.Account.AccountDto;
import us.dev.backend.common.ErrorsResource;
import us.dev.backend.configs.AppConfig;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping(value = "/api/post")
public class PostController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    S3Service s3Service;

    @Autowired
    AppConfig appConfig;

    @PostMapping("/upload")
    public ResponseEntity createPost(@RequestBody @Valid PostDto postDto, Errors errors,
                                      MultipartFile file) throws IOException {
        if(errors.hasErrors()) {
            return badRequest(errors);
        }

        String filePath = s3Service.upload(file);
        Post post = this.appConfig.modelMapper().map(postDto, Post.class);

        post.setFilePath(filePath);

        postRepository.save(post);


        return ResponseEntity.ok(errors);

    }

    @PostMapping("/test")
    public String createPostTest(
                                     MultipartFile file) throws IOException {

        String filePath = s3Service.upload(file);
        System.out.println("################");

        return filePath;


    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
