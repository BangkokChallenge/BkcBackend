package us.dev.backend.Comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import us.dev.backend.Post.PostRepository;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

//    public CommentDto getAllComments(Integer id){
//        Comment entity = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
////        return new CommentDto(entity);
//    }
}
