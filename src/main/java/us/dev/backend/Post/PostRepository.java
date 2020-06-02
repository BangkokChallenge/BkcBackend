package us.dev.backend.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PostRepository extends JpaRepository<Post, Integer> {

    /* Post 리스트 전체 검색 (생성날짜 내림차순) */
    List<Post> findAllByOrderByCreatedAtDesc();
    /* 내가 작성한 Post 검색 (생성날짜 내림차순) */
    List<Post> findByAccountIdOrderByCreatedAtDesc(String accountId);
    // 해쉬태그를 이용해서 해당 post 리턴하기 (생성날짜 내림차순)
    List<Post> findAllByHashTagsContentOrderByCreatedAtDesc(String content);
}
