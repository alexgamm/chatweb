package chatweb.mapper;

import chatweb.entity.Dictionary;
import chatweb.model.api.DictionaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DictionaryMapper {
    DictionaryMapper INSTANCE = Mappers.getMapper(DictionaryMapper.class);

    DictionaryDto toDictionaryDto(Dictionary dictionary);
}
