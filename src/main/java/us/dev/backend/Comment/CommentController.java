package us.dev.backend.Comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor  // final이 선언된 모든 필드를 인자값으로 하는 생성자를 생성해준다. 그래서 commentService를 사용할 수 있는것!
@RestController
public class CommentController {
    private final CommentService commentService;

    // 리스트 조회
    @GetMapping("/{feedId}/post/{postId}/comment")
    public String findById(@PathVariable Integer feedId, @PathVariable Integer postId) {
        // 피드 id + 포스트 id로 포스트에 달린 코멘트 리스트를 가져온다.
        return null;
    }
//
//    // 댓글 등록
//    @PostMapping("/post/{postId}/comment")
//    public Long save(@RequestBody PostsSaveRequestDto requestDto) {
//        return postsService.save(requestDto);
//    }
//
//    // 단건조회
//    @GetMapping("/post/{postId}/comment/{commentId}")
//    public PostsResponseDto findById(@PathVariable Long postId, @PathVariable Long commentId) {
//        return postsService.findById(id);
//    }
//
//    // 댓글 수정
//    @PutMapping("/post/{postId}/comment/{commentId}")
//    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
//        return postsService.update(id, requestDto);
//    }
//
//    // 댓글 삭제
//    @DeleteMapping("/post/{postId}/comment/{commentId}")
//    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
//        return postsService.update(id, requestDto);
//    }
}
