package us.dev.backend.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import us.dev.backend.Post.Post;

import java.util.List;

// Comment 클래스로 database를 접근하게 해줄 클래스
// db layer 접근자로서 jpa에선 repository라고 부르며 인터페이스로 생성한다.
// 단순히 인터페이스를 생성 후, JpaRepository<Entity 클래스, PK타입> 을 상속하면 기본적인 crud 메소드가 자동으로 생성된다.
// Entity 클래스와 Entity repository는 함께 위치해야 한다.
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);

    long countByPost(Integer postId);
}

