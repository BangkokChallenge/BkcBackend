package us.dev.backend.HashTag;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    List<HashTag> findByContentContaining(String searchKeyword, Pageable paging);
    Optional<HashTag> findByContent(String content);
}
