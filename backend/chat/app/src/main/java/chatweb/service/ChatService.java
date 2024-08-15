package chatweb.service;

import chatweb.chat.ChatServiceGrpc;
import chatweb.chat.ChatServiceOuterClass;
import chatweb.entity.Message;
import chatweb.entity.Room;
import chatweb.entity.User;
import chatweb.mapper.MessageMapper;
import chatweb.model.dto.MessageDto;
import chatweb.model.event.NewMessageEvent;
import chatweb.model.message.Button;
import chatweb.producer.EventsKafkaProducer;
import chatweb.repository.MessageRepository;
import chatweb.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static chatweb.model.api.ApiError.badRequest;
import static java.util.UUID.randomUUID;

@GrpcService
@RequiredArgsConstructor
public class ChatService extends ChatServiceGrpc.ChatServiceImplBase {

    private static final TypeReference<List<Button>> BUTTON_LIST_TR = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final RoomsService roomsService;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final EventsKafkaProducer eventsProducer;

    @Override
    public void createRoom(
            ChatServiceOuterClass.CreateRoomRequest request,
            StreamObserver<ChatServiceOuterClass.CreateRoomResponse> responseObserver
    ) {
        if (!userRepository.existsById(request.getCreatorId())) {
            responseObserver.onError(badRequest("Creator not found").toException());
            return;
        }
        Room room = roomsService.createRoom(request.getCreatorId(), request.getPassword(), request.getPrefix());
        responseObserver.onNext(
                ChatServiceOuterClass.CreateRoomResponse.newBuilder()
                        .setId(room.getId())
                        .setKey(room.getKey())
                        .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void sendMessage(ChatServiceOuterClass.SendMessageRequest request, StreamObserver<ChatServiceOuterClass.MessageIdResponse> responseObserver) {
        User user = userRepository.findUserById(request.getUserId());
        if (user == null) {
            responseObserver.onError(badRequest("Message sender not found").toException());
            return;
        }
        List<Button> buttons = Collections.emptyList();
        if (request.hasButtonsJson()) {
            try {
                buttons = objectMapper.readValue(request.getButtonsJson(), BUTTON_LIST_TR);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        Message repliedMessage = Optional.of(request.hasRepliedMessageId())
                .filter(has -> has)
                .map(has -> request.getRepliedMessageId())
                .flatMap(messageRepository::findById)
                .orElse(null);
        Message message = messageRepository.save(new Message(
                randomUUID().toString(),
                request.getMessage().trim(),
                null,
                user,
                Collections.emptySet(),
                repliedMessage,
                new Date(),
                buttons
        ));
        MessageDto messageDto = MessageMapper.messageToMessageDto(message, user.getId(), false);
        eventsProducer.addEvent(new NewMessageEvent(messageDto));
        responseObserver.onNext(
                ChatServiceOuterClass.MessageIdResponse.newBuilder()
                        .setMessageId(messageDto.getId())
                        .build()
        );
        responseObserver.onCompleted();
    }
}
