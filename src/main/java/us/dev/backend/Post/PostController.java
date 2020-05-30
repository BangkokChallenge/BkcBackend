package us.dev.backend.Post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional
    @PostMapping("/upload")
    public ResponseEntity uploadPost(List<MultipartFile> fileList, PostDto postDto) throws Exception {
        /* 현재 사용자 받아오기 */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String getUsername = authentication.getName();

        Account getAccount = this.accountRepository.findById(getUsername)
                .orElseThrow(() -> new UsernameNotFoundException("계정 정보가 잘못됨"));

        if(fileList.isEmpty()) {
            throw new IllegalArgumentException("파일이 첨부되지 않았음");
        }

        String filePath = "";

        for(int i=0;i<fileList.size();i++) {
            String streamFilePath = s3Service.upload(fileList.get(i));
            if(i == fileList.size()-1) {
                filePath = filePath + streamFilePath;
            }
            else {
                filePath = filePath + streamFilePath + ",";
            }
        }

        /* 저장할 POST Setting */

        Post post = this.appConfig.modelMapper().map(postDto, Post.class);


        List<HashTag> hashTags = new ArrayList<>();
        if(postDto.getHashTag() != null) {
            String[] strHashTags = postDto.getHashTag().replaceAll(" ", "").split("#");
            for(int i =1; i < strHashTags.length; i++) {
                HashTag hashTag = new HashTag();
                hashTag.setContent("#"+strHashTags[i]);
                hashTag.setAccount(getAccount);
                hashTags.add(hashTag);
            }
            post.setHashTag(hashTags);
        }
        post.setAccountId(getAccount.getId());
        post.setNickname(getAccount.getNickname());
        post.setProfile_photo(getAccount.getProfile_photo());
        post.setFilePath(filePath);
        post.setCommentCount(0);
        post.setLikeCount(0);

        Post newPost = postRepository.save(post);

        LikePost newLike = LikePost.builder()
                .likeState(false)
                .postId(newPost.getId())
                .accountId(newPost.getAccountId())
                .build();

        likeRepository.save(newLike);

        // header location 용 uri
        ControllerLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash("upload");
        URI createdUri = selfLinkBuilder.toUri();

        PostResource postResource = new PostResource(newPost);
        postResource.add(linkTo(PostController.class).slash("upload").withSelfRel());
        postResource.add(linkTo(PostController.class).slash("").withRel("getPostAllList"));
        postResource.add(linkTo(PostController.class).slash("getMyPosts").withRel("getMyPosts"));
        postResource.add(linkTo(PostController.class).slash("getMyLikes").withRel("getMyLikes"));
        postResource.add(new Link("/docs/index.html#resource-uploadPost").withRel("profile"));

        //created(uri)는 리턴하면 header location 내 createUri 가 들어가게됨.
        return ResponseEntity.created(createdUri).body(postResource);

    }

    /* Post 전체 List */
    @GetMapping
    public ResponseEntity getPostList(@PageableDefault Pageable pageable, PagedResourcesAssembler<Post> assembler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String getUsername = authentication.getName();

        List<Post> postList = this.postRepository.findAllByOrderByCreatedAtDesc();

        if(postList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        postList.stream().forEach(post -> {

            LikePost getLike = this.likeRepository.findByAccountIdAndPostId(getUsername, post.getId());

            if (getLike == null) {
                post.setSelfLike(false);
            } else {
                if (getLike.isLikeState()) {
                    post.setSelfLike(true);
                } else {
                    post.setSelfLike(false);
                }
            }

        });

        //TODO 맨뒤(3번쨰) 인자를 통해서 리턴하는 최대 개수를 설정할 수 있음
        int pageStart = (int)pageable.getOffset();
        int pageEnd = (pageStart + pageable.getPageSize()) > postList.size() ? postList.size() : (pageStart + pageable.getPageSize());
        Page<Post> postFeed = new PageImpl<>(postList.subList(pageStart, pageEnd), pageable, postList.size());

        var pagedResources = assembler.toResource(postFeed);

        ControllerLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash("");
        URI createdUri = selfLinkBuilder.toUri();

//        pagedResources.add(linkTo(PostController.class).slash("").withSelfRel());
        pagedResources.add(linkTo(PostController.class).slash("upload").withRel("postUpload"));
        pagedResources.add(linkTo(PostController.class).slash("getMyPosts").withRel("getMyPosts"));
        pagedResources.add(linkTo(PostController.class).slash("getMyLikes").withRel("getMyLikes"));
        pagedResources.add(new Link("/docs/index.html#resource-getPostList").withRel("profile"));


        return ResponseEntity.created(createdUri).body(pagedResources);
    }

    /* 내가 작성한 Post List */
    @GetMapping("/getMyPosts")
    public ResponseEntity getMyPosts(@PageableDefault Pageable pageable, PagedResourcesAssembler<Post> assembler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String getUsername = authentication.getName();

        List<Post> postList = postRepository.findByAccountIdOrderByCreatedAtDesc(getUsername);

        postList.stream().forEach(post -> {

            LikePost getLike = this.likeRepository.findByAccountIdAndPostId(getUsername, post.getId());

            if (getLike == null) {
                post.setSelfLike(false);
            } else {
                if (getLike.isLikeState()) {
                    post.setSelfLike(true);
                } else {
                    post.setSelfLike(false);
                }
            }

        });

        int pageStart = (int)pageable.getOffset();
        int pageEnd = (pageStart + pageable.getPageSize()) > postList.size() ? postList.size() : (pageStart + pageable.getPageSize());
        Page<Post> postFeed = new PageImpl<>(postList.subList(pageStart, pageEnd), pageable, postList.size());

        var pagedResources = assembler.toResource(postFeed);

        ControllerLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash("");
        URI createdUri = selfLinkBuilder.toUri();

        pagedResources.add(linkTo(PostController.class).slash("upload").withRel("postUpload"));
        pagedResources.add(linkTo(PostController.class).slash("").withRel("getPostAllList"));
        pagedResources.add(linkTo(PostController.class).slash("getMyLikes").withRel("getMyLikes"));
        pagedResources.add(new Link("/docs/index.html#resource-getMyPosts").withRel("profile"));

        return ResponseEntity.created(createdUri).body(pagedResources);
    }

    /* 내가 좋아요한 Post List */
    @GetMapping("/getMyLikes")
    public ResponseEntity getMyLikes(@PageableDefault Pageable pageable, PagedResourcesAssembler<Post> assembler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String getUsername = authentication.getName();

        List<LikePost> likePostList = likeRepository.findByAccountIdAndLikeStateOrderByCreatedAtDesc(getUsername,true);
        if(likePostList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Post> postList = new ArrayList<>();

        likePostList.stream().forEach(likePost -> {
            Optional<Post> getOptionalPost = postRepository.findById(likePost.getPostId());
            if(getOptionalPost.isEmpty()) {
                throw new IllegalArgumentException("해당 포스트와 연결된 것이 없음");
            }
            Post getPost = getOptionalPost.get();
            getPost.setSelfLike(true);
            postList.add(getPost);
        });


        int pageStart = (int)pageable.getOffset();
        int pageEnd = (pageStart + pageable.getPageSize()) > postList.size() ? postList.size() : (pageStart + pageable.getPageSize());
        Page<Post> postFeed = new PageImpl<>(postList.subList(pageStart, pageEnd), pageable, postList.size());

        var pagedResources = assembler.toResource(postFeed);

        ControllerLinkBuilder selfLinkBuilder = linkTo(PostController.class).slash("");
        URI createdUri = selfLinkBuilder.toUri();

        pagedResources.add(linkTo(PostController.class).slash("upload").withRel("postUpload"));
        pagedResources.add(linkTo(PostController.class).slash("").withRel("getPostAllList"));
        pagedResources.add(linkTo(PostController.class).slash("getMyPosts").withRel("getMyPosts"));
        pagedResources.add(new Link("/docs/index.html#resource-getMyLikes").withRel("profile"));

        return ResponseEntity.created(createdUri).body(pagedResources);
    }


    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
