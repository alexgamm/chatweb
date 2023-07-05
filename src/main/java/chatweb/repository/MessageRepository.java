package chatweb.repository;

import chatweb.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findBySendDateBeforeOrderBySendDateDesc(Date sendDate, Pageable pageable);

    List<Message> findByOrderBySendDateDesc(Pageable pageable);
    Message findMessageById(String id);
    void deleteMessageById(String id);
}
