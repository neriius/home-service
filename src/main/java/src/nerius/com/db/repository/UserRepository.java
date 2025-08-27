package src.nerius.com.db.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import src.nerius.com.db.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByLogin(String login);
    boolean existsUserByLogin(String login);
}
