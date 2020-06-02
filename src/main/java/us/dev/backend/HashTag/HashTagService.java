package us.dev.backend.HashTag;

import java.util.List;
import java.util.Optional;

public interface HashTagService {
    List<HashTag> getHashTagList(String searchKeyword);
    Optional<HashTag> findByContent(String content);
}
