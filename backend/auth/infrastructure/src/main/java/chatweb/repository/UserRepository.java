package chatweb.repository;

import chatweb.entity.User;
import chatweb.model.Color;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Deprecated(forRemoval = true)
    User findUserByUsername(String username);

    boolean existsByUsername(String username);

    @Query("select u from User u where lower(u.email) = lower(:email)")
    User findUserByEmail(String email);

    User findUserById(int id);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.password = :password where u.id = :userId")
    void updatePassword(int userId, String password);

    boolean existsByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.username = :newUsername where u.id =:userId")
    void updateUsername(int userId, String newUsername);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.color = :color where u.id =:userId")
    void updateColor(int userId, Color color);
}
