package us.dev.backend.HashTag;

import lombok.*;
import us.dev.backend.Account.Account;
import us.dev.backend.Post.Post;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class HashTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

    @Builder.Default
    @ManyToMany(mappedBy = "hashTag")
    List<Post> post = new ArrayList<>();

    @ManyToOne
    Account account;
}

