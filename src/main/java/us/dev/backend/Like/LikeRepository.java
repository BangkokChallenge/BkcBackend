package us.dev.backend.Like;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public interface LikeRepository extends JpaRepository<Like, Integer> {

    //해당 사용자가 이 post를 좋아요 했는지 여부
    Optional<Like> findByAccountIdAndPostId(Integer accountId, Integer postId);

    //좋아요 수
    long countByPostId(Integer postId);
}
