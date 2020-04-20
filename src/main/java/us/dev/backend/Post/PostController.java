package us.dev.backend.Post;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import us.dev.backend.Account.AccountDto;
import us.dev.backend.common.ErrorsResource;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping(value = "/api/account", produces = MediaTypes.HAL_JSON_UTF8_VALUE)
public class PostController {

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid PostDto postDto, Errors errors,
                                      MultipartFile file) throws IOException {
        if(errors.hasErrors()) {
            return badRequest(errors);
        }





    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new ErrorsResource(errors));
    }

}
