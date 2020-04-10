package us.dev.backend.Account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountDto {

    @Id
    private String id;


    private String fcmToken;

    private String kakaoAccessToken;
    private String kakaoRefreshToken;
    private String serviceAccessToken;
    private String serviceRefreshToken;

    @NotNull
    private String username;
    @NotNull
    private String nickname;
    @NotNull
    private String profile_photo;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
