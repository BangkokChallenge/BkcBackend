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

    private String serviceAccessToken;
    private String serviceRefreshToken;

    @NotNull
    private String nickname;
    @NotNull
    private String profile_photo;

}
