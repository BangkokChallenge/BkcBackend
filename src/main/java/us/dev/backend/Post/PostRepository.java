package us.dev.backend.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import us.dev.backend.Account.Account;

import java.util.List;
import java.util.Optional;

@Service
public interface PostRepository extends JpaRepository<Post, Integer> {

    /* Post 리스트 전체 검색 (생성날짜 내림차순) */
    List<Post> findAllByOrderByCreatedAtDesc();
    /* 내가 작성한 Post 검색 (생성날짜 내림차순) */
    List<Post> findByAccountIdOrderByCreatedAtDesc(String accountId);

}
