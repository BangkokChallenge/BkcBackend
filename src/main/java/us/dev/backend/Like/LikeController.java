package us.dev.backend.Like;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountRepository;
import us.dev.backend.Post.Post;
import us.dev.backend.Post.PostRepository;

import java.util.Optional;

@Controller
@RequestMapping(value = "/api/like", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
@CrossOrigin("*")
public class LikeController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    LikeRepository likeRepository;

    @PutMapping("/{postId}")
    public ResponseEntity changeLikeState(@PathVariable Integer postId, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<Account> optionalAccount = this.accountRepository.findById(userDetails.getUsername());
        if(optionalAccount.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Account newAccount = optionalAccount.get();

        LikePost getLike = this.likeRepository.findByAccountIdAndPostId(newAccount.getId(),postId);

        // 없으면 -> 기본 false -> true 로 생성해서 저장.
        if(getLike == null) {
            LikePost newLike = LikePost.builder()
                    .postId(postId)
                    .accountId(newAccount.getId())
                    .likeTrueAndFalse(true)
                    .build();
            this.likeRepository.save(newLike);

            likeCountPlusAndMinus(postId,true);
            return ResponseEntity.ok(newLike);

        } // 있으면 상태값 반대로해서 저장.
        else {
            if(getLike.likeTrueAndFalse) {
                getLike.setLikeTrueAndFalse(false);

                likeCountPlusAndMinus(postId,false);
                this.likeRepository.save(getLike);
            }
            else {
                getLike.setLikeTrueAndFalse(true);
                this.likeRepository.save(getLike);

                likeCountPlusAndMinus(postId,true);
            }
            return ResponseEntity.ok(getLike);
        }
    }

    public void likeCountPlusAndMinus(Integer postId, boolean flag) {
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
    }

}
