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

    @Id @NotNull
    private String id;


    @NotNull
    private String nickname;
    @NotNull
    private String profile_photo;

}
