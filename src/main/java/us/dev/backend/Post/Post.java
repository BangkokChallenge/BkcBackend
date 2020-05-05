package us.dev.backend.Post;

import lombok.*;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
public class Post extends BaseTimeEntity {

    String accountId;

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String article;

    String hashTag;

    String nickname;
    String profile_photo;


    boolean selfLike;

    @Min(0)
    long commentCount;
    long likeCount;


    String filePath;

}
