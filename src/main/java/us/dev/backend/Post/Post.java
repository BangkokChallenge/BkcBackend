package us.dev.backend.Post;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.hateoas.core.Relation;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
public class Post extends BaseTimeEntity {
    String userId;

    @Id @GeneratedValue
    Integer id;

    String hashTag;

    String nickname;
    String profile_photo;

    boolean selfLike;

    @Min(0)
    long commentCount;
    long likeCount;

    String article;

    String filePath;

}
