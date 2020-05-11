package us.dev.backend.Post;

import lombok.*;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
public class Post extends BaseTimeEntity {

    @NotNull
    String accountId;

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String article;

    String hashTag;

    @NotNull
    String nickname;

    @NotNull
    String profile_photo;


    boolean selfLike;

    @Min(0)
    long commentCount;

    @Min(0)
    long likeCount;

    @NotNull
    String filePath;

}
