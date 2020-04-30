package us.dev.backend.Comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import us.dev.backend.Account.Account;
import us.dev.backend.Account.AccountRepository;
import us.dev.backend.Post.Post;
import us.dev.backend.Post.PostRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final AccountRepository accountRepository;


    @Transactional
    public Comment save(Integer postId, CommentDto commentDto, String userId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 post가 없습니다. id=" + postId));
        Account account = accountRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 user가 없습니다. id=" + userId));

        String content = commentDto.getContent();
        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .account(account)
                .build();
        return commentRepository.save(comment);
    }

    public Comment findById(Integer postId, Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + commentId));
    }

    @Transactional(readOnly = true)
    public List<Comment> findAllComments(Integer postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 post가 없습니다. id=" + postId));
        return commentRepository.findAllByPost(post);
    }

    @Transactional
    public void delete(Integer postId, Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + commentId));
        Account account = accountRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 user가 없습니다. id=" + userId));
        if (account.equals(comment.getAccount())) {
            commentRepository.delete(comment);
        } else {
            throw new IllegalArgumentException("해당 user가 없습니다. id=" + userId);
        }
    }

    @Transactional
    public Comment update(Integer postId,
                          Long commentId,
                          CommentDto commentDto,
                          String userId) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + commentId));
        Account account = accountRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("해당 user가 없습니다. id=" + userId));
        if (account.equals(comment.getAccount())) {
            return comment.update(commentDto.getContent());
        } else {
            throw new IllegalArgumentException("해당 user가 없습니다. id=" + userId);
        }
    }
}



