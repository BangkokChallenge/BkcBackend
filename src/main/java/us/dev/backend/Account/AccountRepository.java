package us.dev.backend.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findById(String id);
    Optional<Account> findByUsername(String id);

}
