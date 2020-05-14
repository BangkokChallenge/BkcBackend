package us.dev.backend.Like;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class LikeResource extends Resource<LikePost> {
    public LikeResource(LikePost content, Link... links) {
        super(content, links);
    }
}
