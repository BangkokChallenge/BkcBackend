package us.dev.backend.Like;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;


@Service
public interface LikeRepository extends JpaRepository<LikePost, Integer> {

    //해당 사용자가 이 post를 좋아요 했는지 여부

    //@Query("SELECT l FROM LikePost as l WHERE l.postId = postId AND l.accountId = accountId")
    LikePost findByAccountIdAndPostId(String accountId, Integer postId);

    //좋아요 수
    long countByPostId(Integer postId);
}
