package us.dev.backend.HashTag;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import us.dev.backend.Post.Post;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@JsonSerialize(using = HashTagSerializer.class)
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

    @ManyToMany(mappedBy = "hashTags")
    Set<Post> posts = new HashSet<>();
}

