package us.dev.backend.Like;

import lombok.*;
import us.dev.backend.Post.Post;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@Entity
public class LikePost extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    Integer postId;

    String accountId;

    boolean likeTrueAndFalse;

}
