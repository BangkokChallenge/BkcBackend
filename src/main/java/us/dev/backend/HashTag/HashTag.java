package us.dev.backend.HashTag;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import us.dev.backend.Account.Account;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@JsonSerialize(using = HashTagSerializer.class)
public class HashTag extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

//    @Builder.Default
//    @ManyToMany(mappedBy = "hashTag")
//    List<Post> post = new ArrayList<>();

    @ManyToOne
    Account account;
}

