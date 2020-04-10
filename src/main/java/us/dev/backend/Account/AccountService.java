package us.dev.backend.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    /* Accunt를 저장 할 때, 패스워드를 암호화하여 저장함. */
    public Account saveAccount(Account account) {
        account.setPassword(this.passwordEncoder.encode(account.getPassword()));
        return this.accountRepository.save(account);
    }



    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        /* input 해당하는 Qrid 찾는다. 없으면 orElseThrow로 Exception을 보기좋게 출력함 */
        Account account = accountRepository.findById(id)
                .orElseThrow(()->new UsernameNotFoundException(id));

        /* UserInfo Object -> Spring Security가 이해할 수 있는 UserDatails로 TypeCast */
        /* Token을 발급하기 위해, 아래 값으로 DB 내 고유식별값이 있는지 확인함. */
        return new User(account.getId(),account.getPassword(),authorities(account.getRoles()));
    }

    private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                .collect(Collectors.toSet());
    }
}
