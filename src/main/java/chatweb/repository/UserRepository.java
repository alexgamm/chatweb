package chatweb.repository;

import chatweb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findAllByOrderByLastActivityAtDesc();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User findUserById(int id);

    @Modifying
    @Query("update User u set u.lastActivityAt = CURRENT_TIMESTAMP where u.id = :userId")
    void updateLastActivityAt(int userId);

    boolean existsByEmail(String email);
}
