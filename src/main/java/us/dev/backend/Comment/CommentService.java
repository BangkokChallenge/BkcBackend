package us.dev.backend.Comment;

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
    public Comment save(Integer postId, CommentDto commentDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 post가 없습니다. id=" + postId));
        Account account = accountRepository.findById(commentDto.getAccountId()).orElseThrow(() -> new IllegalArgumentException("해당 user가 없습니다. id=" + postId));

        String content = commentDto.getContent();
        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .account(account)
                .build();
        Comment postedComment = commentRepository.save(comment);
        return postedComment;
    }

    public ResponseEntity findById(Integer postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + commentId));
        return ResponseEntity.ok().body(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> findAllComments(Integer postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 post가 없습니다. id=" + postId));

        List<Comment> comments = commentRepository.findAllByPost(post);
        return comments;
    }

    @Transactional
    public void delete(Integer postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + commentId));
        commentRepository.delete(comment);
    }

    @Transactional
    public ResponseEntity update(Integer postId,
                                 Long commentId,
                                 CommentDto commentDto) {

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + commentId));
        Comment updatedComment = comment.update(commentDto.getContent());
        return ResponseEntity.ok().body(updatedComment);
    }
}



