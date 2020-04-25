package us.dev.backend.Account;

import lombok.*;
import us.dev.backend.common.BaseTimeEntity;

import javax.persistence.*;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Account extends BaseTimeEntity {

    @Id
    private String id;

    private String nickname;
    private String profile_photo;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;

    /*
        여기 필드값 어떻게 넘길지
        최대한 저장안하는 걸로
     */
    String ServiceAccessToken;
    String ServiceRefreshToken;
}
