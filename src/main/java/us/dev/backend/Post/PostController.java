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
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import us.dev.backend.Account.*;
import us.dev.backend.Comment.CommentRepository;
import us.dev.backend.common.ErrorsResource;
import us.dev.backend.configs.AppConfig;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

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
    CommentRepository commentRepository;

    @Autowired
    S3Service s3Service;

    @Autowired
    AppConfig appConfig;

    @PostMapping("/upload")
    public ResponseEntity createPost(PostDto postDto, MultipartFile file,
                                       Authentication authentication) throws IOException {
        String filePath = s3Service.upload(file);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Account> optionalAccount = this.accountRepository.findById(userDetails.getUsername());
        if(optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Account newAccount = optionalAccount.get();

        System.out.println(filePath);

        Post post = this.appConfig.modelMapper().map(postDto, Post.class);
        post.setUserId(newAccount.getId());
        post.setNickname(newAccount.getNickname());
        post.setProfile_photo(newAccount.getNickname());
        post.setFilePath(filePath);

        long commentCount = commentRepository.countByPost(post.id);
        long likeCount = 0;

        post.setCommentCount(commentCount);


        Post newPost = postRepository.save(post);

        ControllerLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash("/upload");
        URI createdUri = selfLinkBuilder.toUri();

        PostResource postResource = new PostResource(newPost);
        postResource.add(new Link("/docs/index.html#resource-createPost").withRel("profile"));

        return ResponseEntity.created(createdUri).body(postResource);

    }

    @GetMapping("/")
    public ResponseEntity getPosts(@PageableDefault Pageable pageable, PagedResourcesAssembler<Post> assembler) {//Authentication authentication {
        Page<Post> postList = this.postRepository.findAll(pageable);

        var pagedResources = assembler.toResource(postList, e-> new PostResource(e));
        pagedResources.add(new Link("/docs/index.html#resource-post-list").withRel("profile"));


        return ResponseEntity.ok(pagedResources);
    }

    @PutMapping("/changeLS/{id}")
    public ResponseEntity changeLikeStatus(@PathVariable Integer id) {

        Optional<Post> optionalPost = postRepository.findById(id);
        Post getPost = optionalPost.get();
        if(getPost.selfLike) {
            getPost.setSelfLike(false);
        }
        else if(getPost.selfLike) {
            getPost.setSelfLike(true);
        }

        Post newPost = postRepository.save(getPost);

        PostResource postResource = new PostResource(newPost);
        postResource.add(new Link("/docs/index.html#resource-changeLikeStatus").withRel("profile"));

        return ResponseEntity.ok(newPost);
    }



    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
