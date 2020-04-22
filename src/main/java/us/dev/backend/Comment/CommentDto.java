package us.dev.backend.Comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
    private String content;
    private String accountId;
}

