package chatweb.repository;

import chatweb.entity.Reaction;
import chatweb.model.dto.ReactionDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, String> {
    Optional<Reaction> findByMessage_IdAndUserIdAndReaction(String messageId, int userId, String reaction);

    @Query(value = """
            SELECT
                reaction,
                COUNT(*) AS count,
                BOOL_OR(user_id = :userId) AS hasOwn
            FROM reactions
            WHERE message_id = :messageId
            GROUP BY reaction;
            """, nativeQuery = true)
    Set<ReactionDto> groupReactions(int userId, String messageId);
}
