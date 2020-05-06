package us.dev.backend.Post;

import lombok.*;
import us.dev.backend.HashTag.HashTag;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

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

    String nickname;
    String profile_photo;


    boolean selfLike;

    @Min(0)
    long commentCount;
    long likeCount;


    String filePath;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "post_hashtag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
    List<HashTag> hashTag = new ArrayList<>();

}
