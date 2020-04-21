package us.dev.backend.Comment;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;

@Getter // 롬복의 어노테이션
@NoArgsConstructor // 롬복의 어노테이션, 기본 생성자 자동 추가
@Entity // jpa의 어노테이션, 테이블과 링크될 클래스임을 나타낸다.
// 실제 DB의 테이블과 매칭될 클래스, Entity 클래스라고도 한다.
// Entity 클래스에서는 절대 setter 메소드를 만들지 않는다!
// 값 변경이 필요할 시 setter 대신 그 목적과 의대롤 나타낼 수 있는 메소드를 추가한다.
public class Comment extends BaseTimeEntity {

    @Id // 테이블의 pk 필드를 나타낸다.
    // pk의 생성 규칙, GenerationType.IDENTITY 이 옵션이 있어야 auto_increment가 된다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private Long post;

    private Long account;

    @Builder // 해당 클래스의 빌더 패턴 클래스를 생성, 생성자 상단에 선언 시 생성자에 포함된 필드만 빌더에 포함
    public Comment(String content, Long account, Long post) {
        this.content = content;
        this.account = account;
        this.post = post;
    }
}
