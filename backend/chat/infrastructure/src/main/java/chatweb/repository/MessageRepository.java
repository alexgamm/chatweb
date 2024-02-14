package chatweb.repository;

import chatweb.entity.Message;
import jakarta.persistence.criteria.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String>, JpaSpecificationExecutor<Message> {

    void deleteMessageById(String id);

    @Query
    default List<Message> findMessages(@NotNull Integer count, @Nullable Integer roomId, @Nullable Long from) {
        return findAll(((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate, roomId == null
                    ? criteriaBuilder.isNull(root.get("room").get("id"))
                    : criteriaBuilder.equal(root.get("room").get("id"), roomId));
            if (from != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThan(root.get("sendDate"), new Date(from))
                );
            }
            return predicate;
        }), PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "sendDate"))).getContent();
    }
}
