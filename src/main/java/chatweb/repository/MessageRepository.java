package chatweb.repository;

import chatweb.entity.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    List<Message> findBySendDateBeforeOrderBySendDateDesc(Date sendDate, Pageable pageable);

    List<Message> findByOrderBySendDateDesc(Pageable pageable);

    @Modifying
    @Query("update Message m set m.message = :message where m.id = :id")
    void updateMessage(String id, String message);

    void deleteMessageById(String id);
}
