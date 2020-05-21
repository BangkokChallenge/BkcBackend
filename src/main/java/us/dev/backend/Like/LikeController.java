package us.dev.backend.Like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountRepository;
import us.dev.backend.Post.Post;
import us.dev.backend.Post.PostController;
import us.dev.backend.Post.PostRepository;
import us.dev.backend.Post.PostResource;

import java.net.URI;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/api/like", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class LikeController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    LikeRepository likeRepository;


    @Transactional
    @PutMapping("/{postId}")
    public ResponseEntity changeLikeState(@PathVariable Integer postId) {
        /* 현재 사용자 받아오기 */
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String getUsername = authentication.getName();

        Optional<Account> optionalAccount = this.accountRepository.findById(getUsername);
        if(optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account newAccount = optionalAccount.get();

        LikePost getLike = this.likeRepository.findByAccountIdAndPostId(getUsername,postId);

        // 없으면 -> 기본 false -> true 로 생성해서 저장.
        if(getLike == null) {
            LikePost newLike = LikePost.builder()
                    .postId(postId)
                    .accountId(newAccount.getId())
                    .likeState(true)
                    .build();
            this.likeRepository.save(newLike);

            long getLikeCount = likeCountPlusAndMinus(postId,true);
            newLike.setLikeCount(getLikeCount);

            /* HATEOAS */
            ControllerLinkBuilder selfLinkBuilder = linkTo(LikeController.class).slash("like").slash(newLike.getPostId());
            URI createdUri = selfLinkBuilder.toUri();

            LikeResource likeResource = new LikeResource(newLike);
            likeResource.add(linkTo(LikeController.class).slash(newLike.getPostId()).withSelfRel());
            likeResource.add(linkTo(PostController.class).slash("").withRel("Post list"));
            likeResource.add(new Link("/docs/index.html#resource-changeLikeState").withRel("profile"));

            return ResponseEntity.created(createdUri).body(likeResource);

        } // 있으면 상태값 반대로해서 저장.
        else {
            if(getLike.isLikeState()) {
                getLike.setLikeState(false);
                this.likeRepository.save(getLike);

                long getLikeCount = likeCountPlusAndMinus(postId,false);
                getLike.setLikeCount(getLikeCount);
            }
            else {
                getLike.setLikeState(true);
                this.likeRepository.save(getLike);

                long getLikeCount = likeCountPlusAndMinus(postId,true);
                getLike.setLikeCount(getLikeCount);
            }


            ControllerLinkBuilder selfLinkBuilder = linkTo(LikeController.class).slash("like").slash(getLike.getPostId());
            URI createdUri = selfLinkBuilder.toUri();

            LikeResource likeResource = new LikeResource(getLike);
            likeResource.add(linkTo(LikeController.class).slash(getLike.getPostId()).withSelfRel());
            likeResource.add(linkTo(PostController.class).slash("").withRel("Post list"));
            likeResource.add(new Link("/docs/index.html#resource-changeLikeState").withRel("profile"));


            return ResponseEntity.created(createdUri).body(likeResource);
        }
    }

    public long likeCountPlusAndMinus(Integer postId, boolean flag) {
        Optional<Post> getPostOptional = this.postRepository.findById(postId);
        Post getPost = getPostOptional.get();

        //true이면 +
        if(flag) {
            getPost.setLikeCount(getPost.getLikeCount()+1);
            postRepository.save(getPost);
        }
        //false이면 -
        else {
            getPost.setLikeCount(getPost.getLikeCount()-1);
            postRepository.save(getPost);
        }
        return getPost.getLikeCount();
    }

}
