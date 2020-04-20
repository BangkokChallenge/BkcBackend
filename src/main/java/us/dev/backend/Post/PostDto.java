package us.dev.backend.Post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostDto {
    @Id
    Integer post_Index;

    String article;
    String hasgTag[];
    String file;

    Integer likeNum;
    boolean selfLike;
    Integer commentNum;

    String filePath;
    Integer fileNum;

}
