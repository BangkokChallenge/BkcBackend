package us.dev.backend.Post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import us.dev.backend.Account.*;
import us.dev.backend.Comment.CommentRepository;
import us.dev.backend.Like.LikePost;
import us.dev.backend.Like.LikeRepository;
import us.dev.backend.common.ErrorsResource;
import us.dev.backend.configs.AppConfig;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/post")
@CrossOrigin("*")
public class PostController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    S3Service s3Service;

    @Autowired
    AppConfig appConfig;

    @PostMapping("/upload")
    public ResponseEntity createPost(MultipartFile file, PostDto postDto,
                                     Authentication authentication) throws IOException {

        /* 현재 접속중인 사용자 정보 얻어오기 (Bearer Token 기반) */
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Account> optionalAccount = this.accountRepository.findById(userDetails.getUsername());
        if(optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Account newAccount = optionalAccount.get();

        String filePath = s3Service.upload(file);
        /* 저장할 POST Setting */

        Post post = this.appConfig.modelMapper().map(postDto, Post.class);
        post.setAccountId(newAccount.getId());
        post.setNickname(newAccount.getNickname());
        post.setProfile_photo(newAccount.getNickname());
        post.setFilePath(filePath);
        post.setCommentCount(0);
        post.setLikeCount(0);

        //TODO
        //Hashtag 저장해야함. 1:다
        Post newPost = postRepository.save(post);

        LikePost newLike = LikePost.builder()
                .likeTrueAndFalse(false)
                .postId(newPost.getId())
                .accountId(newPost.getAccountId())
                .build();

        likeRepository.save(newLike);

        ControllerLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash("/upload");
        URI createdUri = selfLinkBuilder.toUri();

        PostResource postResource = new PostResource(newPost);
        postResource.add(new Link("/docs/index.html#resource-post-upload").withRel("profile"));

        return ResponseEntity.created(createdUri).body(postResource);

    }

    @GetMapping
    public ResponseEntity getPosts(@PageableDefault Pageable pageable, PagedResourcesAssembler<Post> assembler) {
        //TODO 개수 제한하는 방법 알아야함. 다받으면 개수가 너무많음.
        List<Post> postList = this.postRepository.findAll();

        postList.stream().forEach(post -> {

            LikePost getLike = this.likeRepository.findByAccountIdAndPostId(post.getAccountId(),post.getId());

            if(getLike == null) {
                post.setSelfLike(false);
            }
            else {
                if(getLike.isLikeTrueAndFalse()) {
                    post.setSelfLike(true);
                }
                else {
                    post.setSelfLike(false);
                }
            }

        });

        Page<Post> postFeed = new PageImpl<>(postList,pageable,postList.size());

        var pagedResources = assembler.toResource(postFeed);
        pagedResources.add(new Link("/docs/index.html#resource-post-list").withRel("profile"));


        return ResponseEntity.ok(pagedResources);
    }


    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
