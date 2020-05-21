package us.dev.backend.Like;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface LikeRepository extends JpaRepository<LikePost, Integer> {

    //해당 사용자가 이 post를 좋아요 했는지 여부

    LikePost findByAccountIdAndPostId(String accountId, Integer postId);

    List<LikePost> findByAccountIdAndLikeStateOrderByCreatedAtDesc(String accountId,boolean state);

}
