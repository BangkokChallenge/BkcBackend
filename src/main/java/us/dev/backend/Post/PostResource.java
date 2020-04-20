package us.dev.backend.Post;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class PostResource extends Resource<Post> {
    public PostResource(Post content, Link... links) {
        super(content, links);
        add(linkTo(PostController.class).withSelfRel());
    }
}
