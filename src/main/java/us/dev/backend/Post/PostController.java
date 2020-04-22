package us.dev.backend.Post;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountDto;
import us.dev.backend.Account.AccountRepository;
import us.dev.backend.Account.AccountService;
import us.dev.backend.common.ErrorsResource;
import us.dev.backend.configs.AppConfig;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping(value = "/api/post")
public class PostController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostService postService;

    @Autowired
    AccountRepository accountRepository;

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

        Optional<Account> optionalAccount = this.accountRepository.findById(postDto.id);
        if(optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Account newAccount = optionalAccount.get();

        String filePath = s3Service.upload(file);
        Post post = this.appConfig.modelMapper().map(postDto, Post.class);
        post.setId(newAccount.getId());
        post.setNickname(newAccount.getNickname());
        post.setProfile_photo(newAccount.getNickname());
        post.setFilePath(filePath);


        Post newPost = postRepository.save(post);

        return ResponseEntity.ok().body(newPost);

    }

    @GetMapping("/list")
    public ResponseEntity list(@PageableDefault Pageable pageable, PagedResourcesAssembler<Post> assembler) {
        Page<Post> postList = this.postRepository.findAll(pageable);
        var pagedResources = assembler.toResource(postList, e-> new PostResource(e));

        pagedResources.add(new Link("/docs/index.html#resource-post-list").withRel("profile"));


        return ResponseEntity.ok(pagedResources);
    }



    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
