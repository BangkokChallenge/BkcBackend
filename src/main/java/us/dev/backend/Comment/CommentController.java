package us.dev.backend.Comment;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor  // final이 선언된 모든 필드를 인자값으로 하는 생성자를 생성해준다. 그래서 commentService를 사용할 수 있는것!
@RestController
@RequestMapping(value = "/api/post")
public class CommentController {
    private final CommentService commentService;

    // 댓글 등록
    @PostMapping("/{postId}/comment")
    public Comment save(@PathVariable Integer postId, @RequestBody CommentDto commentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return commentService.save(postId, commentDto, userId);
    }

    // 리스트 조회
    @GetMapping("/{postId}/comment")
    public List<Comment> findAllComments(@PathVariable Integer postId) {
        return commentService.findAllComments(postId);
    }

    // 단건조회
    @GetMapping("/{postId}/comment/{commentId}")
    public Comment findById(@PathVariable Integer postId, @PathVariable Long commentId) {
        return commentService.findById(postId, commentId);
    }

    // 댓글 삭제
    @DeleteMapping("/{postId}/comment/{commentId}")
    public void delete(@PathVariable Integer postId, @PathVariable Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        commentService.delete(postId, commentId, userId);
    }

    // 댓글 수정
    @PutMapping("/{postId}/comment/{commentId}")
    public Comment update(@PathVariable Integer postId,
                          @PathVariable Long commentId,
                          @RequestBody CommentDto commentDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return commentService.update(postId, commentId, commentDto, userId);
    }
}

