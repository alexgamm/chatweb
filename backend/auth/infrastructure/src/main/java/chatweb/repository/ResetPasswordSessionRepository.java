package chatweb.repository;

import chatweb.entity.ResetPasswordSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordSessionRepository extends CrudRepository<ResetPasswordSession, String> {
}
