package chatweb.mapper;

import chatweb.entity.Message;
import chatweb.model.dto.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(target = "userId", expression = "java(message.getUser().getId())")
    @Mapping(target = "username", expression = "java(message.getUser().getUsername())")
    @Mapping(target = "room", expression = "java(message.getRoomKey())")
    @Mapping(target = "repliedMessage", qualifiedByName = "toDtoWithoutReplyMessage")
    MessageDto toDto(Message message);

    @Mapping(target = "userId", expression = "java(message.getUser().getId())")
    @Mapping(target = "username", expression = "java(message.getUser().getUsername())")
    @Mapping(target = "room", expression = "java(message.getRoomKey())")
    @Mapping(target = "repliedMessage", ignore = true)
    @Named("toDtoWithoutReplyMessage")
    MessageDto toDtoWithoutReplyMessage(Message message);
}
