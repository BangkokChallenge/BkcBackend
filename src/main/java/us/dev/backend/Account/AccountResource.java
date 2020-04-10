package us.dev.backend.Account;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

public class AccountResource extends Resource<Account> {
    public AccountResource(Account content, Link... links) {
        super(content, links);
        add(linkTo(AccountController.class).withSelfRel());
    }
}
