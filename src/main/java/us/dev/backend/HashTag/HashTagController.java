package us.dev.backend.HashTag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/hashtag")
public class HashTagController {

    @Autowired
    HashTagService hashTagService;

    @GetMapping
    public List<HashTag> getBoardList(@RequestParam("keyword") String searchKeyword) {
        return hashTagService.getHashTagList(searchKeyword);
    }
}
