package us.dev.backend.Like;

import lombok.*;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Like extends BaseTimeEntity {

    @Id @GeneratedValue
    Integer id;

    Integer postId;

    Integer accountId;

    boolean likeTrueAndFalse;


}
