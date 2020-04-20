package us.dev.backend.Account;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
public class Account {

    @Id
    private String id;

    private String fcmToken;

    private String serviceAccessToken;
    private String serviceRefreshToken;

    private String nickname;
    private String profile_photo;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
