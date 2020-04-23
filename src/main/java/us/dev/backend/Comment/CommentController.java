package us.dev.backend.Comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor  // final이 선언된 모든 필드를 인자값으로 하는 생성자를 생성해준다. 그래서 commentService를 사용할 수 있는것!
@RestController
public class CommentController {
    private final CommentService commentService;

    // 댓글 등록
    @CrossOrigin(origins = "*")
    @PostMapping("/post/{postId}/comment")
    public Comment save(@PathVariable Integer postId, @RequestBody CommentDto commentDto) {
        return commentService.save(postId, commentDto);
    }

    // 리스트 조회
    @GetMapping("/post/{postId}/comment")
    public List<Comment> findAllComments(@PathVariable Integer postId) {
        return commentService.findAllComments(postId);
    }

    // 단건조회
    @GetMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity findById(@PathVariable Integer postId, @PathVariable Long commentId) {
        return commentService.findById(postId, commentId);
    }

    // 댓글 삭제
    @DeleteMapping("/post/{postId}/comment/{commentId}")
    public void delete(@PathVariable Integer postId, @PathVariable Long commentId) {
        commentService.delete(postId, commentId);
    }

    // 댓글 수정
    @PutMapping("/post/{postId}/comment/{commentId}")
    public ResponseEntity update(@PathVariable Integer postId,
                                 @PathVariable Long commentId,
                                 @RequestBody CommentDto commentDto) {
        return commentService.update(postId, commentId, commentDto);
    }
}

