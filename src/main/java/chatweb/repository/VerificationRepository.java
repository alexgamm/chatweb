package chatweb.repository;

import chatweb.entity.Verification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface VerificationRepository extends JpaRepository<Verification, Integer> {

    Verification findByUserId(int userId);

    @Modifying
    @Query("update Verification v set v.verified = true where v.userId = :userId")
    void updateVerified(int userId);
}
