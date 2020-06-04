package us.dev.backend.HashTag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HashTagServiceImpl implements HashTagService {
    @Autowired
    HashTagRepository hashTagRepository;

    @Override
    public List<HashTag> getHashTagList(String searchKeyword) {
        Pageable pageable = PageRequest.of(0, 5);
        return hashTagRepository.findByContentContaining(searchKeyword, pageable);
    }

    @Override
    public Optional<HashTag> findByContent(String content) {
        return hashTagRepository.findByContent(content);
    }
}
