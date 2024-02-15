package chatweb.repository;

import chatweb.entity.User;
import chatweb.model.user.UserColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User findUserById(int id);

    @Modifying
    @Query("update User u set u.password = :password where u.id = :userId")
    void updatePassword(String password, int userId);

    boolean existsByEmail(String email);

    @Modifying
    @Query("update User u set u.username = :newUsername where u.id =:userId")
    void updateUsername(int userId, String newUsername);

    @Modifying
    @Query("update User u set u.color = :color where u.id =:userId")
    void updateColor(int userId, UserColor color);
}
