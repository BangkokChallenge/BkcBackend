package us.dev.backend.Post;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;


public class PostResource extends Resource<Post> {
    public PostResource(Post content, Link... links) {
        super(content, links);
        //add(linkTo(PostController.class).withSelfRel());
        /* 여기서 항상 출력해줄 href를 추가해줄 수 있음. */
    }
}
