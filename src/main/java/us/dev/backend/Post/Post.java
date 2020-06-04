package us.dev.backend.Post;

import lombok.*;
import us.dev.backend.HashTag.HashTag;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String article;

    String nickname;

    @NotNull
    String profile_photo;

    @Transient
    boolean selfLike;

    @Min(0)
    long commentCount;

    @Min(0)
    long likeCount;

    @NotNull
    String filePath;

    @Builder.Default
    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "post_hashtag", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
    Set<HashTag> hashTags = new HashSet<>();

    public void addHashTag(HashTag hashTag) {
        this.hashTags.add(hashTag);
    }

}
