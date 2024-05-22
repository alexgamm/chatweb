package chatweb.repository;

import chatweb.entity.Dictionary;
import chatweb.model.api.DictionaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, String> {
    @Query("select new chatweb.model.api.DictionaryDto(d.id, d.name) from Dictionary d")
    List<DictionaryDto> findAllDto();
}
