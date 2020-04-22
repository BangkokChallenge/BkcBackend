package us.dev.backend.Post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import us.dev.backend.Account.Account;

@Service
public interface PostRepository extends JpaRepository<Post, String> {
    Page<Post> findAll(Pageable pageable);

}
