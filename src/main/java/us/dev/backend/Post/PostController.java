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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountRepository;
import us.dev.backend.Comment.CommentRepository;
import us.dev.backend.HashTag.HashTag;
import us.dev.backend.Like.LikePost;
import us.dev.backend.Like.LikeRepository;
import us.dev.backend.common.ErrorsResource;
import us.dev.backend.configs.AppConfig;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/api/post")
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
    public ResponseEntity uploadPost(MultipartFile file, PostDto postDto) throws IOException {
        /* 현재 사용자 받아오기 */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String getUsername = authentication.getName();

        Optional<Account> optionalAccount = this.accountRepository.findById(getUsername);

        if (optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Account newAccount = optionalAccount.get();

        String filePath = s3Service.upload(file);
        /* 저장할 POST Setting */

        Post post = this.appConfig.modelMapper().map(postDto, Post.class);


        List<HashTag> hashTags = new ArrayList<>();
        if(postDto.getHashTag() != null) {
            String[] strHashTags = postDto.getHashTag().replaceAll(" ", "").split("#");
            for(int i =1; i < strHashTags.length; i++) {
                HashTag hashTag = new HashTag();
                hashTag.setContent("#"+strHashTags[i]);
                hashTag.setAccount(newAccount);
                hashTags.add(hashTag);
            }
            post.setHashTag(hashTags);
        }
        post.setAccountId(newAccount.getId());
        post.setNickname(newAccount.getNickname());
        post.setProfile_photo(newAccount.getProfile_photo());
        post.setFilePath(filePath);
        post.setCommentCount(0);
        post.setLikeCount(0);

        Post newPost = postRepository.save(post);

        LikePost newLike = LikePost.builder()
                .likeTrueAndFalse(false)
                .postId(newPost.getId())
                .accountId(newPost.getAccountId())
                .build();

        likeRepository.save(newLike);

        // header location 용 uri
        ControllerLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash("upload");
        URI createdUri = selfLinkBuilder.toUri();

        PostResource postResource = new PostResource(newPost);
        postResource.add(linkTo(PostController.class).slash("upload").withSelfRel());
        postResource.add(linkTo(PostController.class).slash("").withRel("post list"));
        postResource.add(new Link("/docs/index.html#resource-uploadPost").withRel("profile"));

        //created(uri)는 리턴하면 header location 내 createUri 가 들어가게됨.
        return ResponseEntity.created(createdUri).body(postResource);

    }

    @GetMapping
    public ResponseEntity getPostList(@PageableDefault Pageable pageable, PagedResourcesAssembler<Post> assembler) {
        List<Post> postList = this.postRepository.findAllByOrderByCreatedAtDesc();

        postList.stream().forEach(post -> {

            LikePost getLike = this.likeRepository.findByAccountIdAndPostId(post.getAccountId(), post.getId());

            if (getLike == null) {
                post.setSelfLike(false);
            } else {
                if (getLike.isLikeTrueAndFalse()) {
                    post.setSelfLike(true);
                } else {
                    post.setSelfLike(false);
                }
            }

        });

        //TODO 이부분 첫번째 인자를 고쳐야함 0-10 이라 10개 고정인듯
        //TODO 맨뒤(3번쨰) 인자를 통해서 리턴하는 최대 개수를 설정할 수 있음
        int pageStart = pageable.getPageNumber()*10;
        int pageEnd = pageStart + 10;
        Page<Post> postFeed = new PageImpl<>(postList.subList(pageStart,pageEnd), pageable, postList.size());

        var pagedResources = assembler.toResource(postFeed);

        ControllerLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash("");
        URI createdUri = selfLinkBuilder.toUri();

        pagedResources.add(linkTo(PostController.class).slash("").withSelfRel());
        pagedResources.add(new Link("/docs/index.html#resource-getPostList").withRel("profile"));


        return ResponseEntity.created(createdUri).body(pagedResources);
    }


    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
