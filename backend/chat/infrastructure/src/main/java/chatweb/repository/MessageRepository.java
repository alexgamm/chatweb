package chatweb.repository;

import chatweb.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, String>, JpaSpecificationExecutor<Message> {

    void deleteMessageById(String id);
}
