package us.dev.backend.Post;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Post {
    String id;

    @Id
    Integer post_Index;

    String hashTag;
    String nickname;
    String profile_photo;
    String article;
    Date writeDate;

    String filePath;

}
