package us.dev.backend.Comment;


import lombok.*;
import us.dev.backend.Account.Account;
import us.dev.backend.Post.Post;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;

//@Getter
//@NoArgsConstructor // 롬복의 어노테이션, 기본 생성자 자동 추가
//@Builder
//@Entity

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Comment extends BaseTimeEntity {

    @Id // 테이블의 pk 필드를 나타낸다.
    // pk의 생성 규칙, GenerationType.IDENTITY 이 옵션이 있어야 auto_increment가 된다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne
    private Post post;

    @ManyToOne
    private Account account;

    public Comment update(String content) {
        this.content = content;
        return this;
    }
}

