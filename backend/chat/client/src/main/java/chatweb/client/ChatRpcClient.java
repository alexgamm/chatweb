package chatweb.client;

import chatweb.chat.ChatServiceGrpc;
import chatweb.chat.ChatServiceOuterClass;
import chatweb.model.message.Button;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatRpcClient {

    private final ObjectMapper objectMapper;
    private final ChatServiceGrpc.ChatServiceBlockingStub grpcClient;

    public ChatRpcClient(
            ObjectMapper objectMapper,
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
            @GrpcClient("chat")
            ChatServiceGrpc.ChatServiceBlockingStub grpcClient
    ) {
        this.objectMapper = objectMapper;
        this.grpcClient = grpcClient;
    }

    public ChatServiceOuterClass.CreateRoomResponse createRoom(int creatorId, String prefix, String password) {
        ChatServiceOuterClass.CreateRoomRequest.Builder builder = ChatServiceOuterClass.CreateRoomRequest.newBuilder()
                .setCreatorId(creatorId)
                .setPrefix(prefix);
        if (password != null) {
            builder.setPassword(password);
        }
        return grpcClient.createRoom(builder.build());
    }

    public String sendMessage(int userId, String message, List<Button> buttons) {
        String buttonsJson;
        try {
            buttonsJson = objectMapper.writeValueAsString(buttons);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return grpcClient.sendMessage(
                ChatServiceOuterClass.SendMessageRequest.newBuilder()
                        .setUserId(userId)
                        .setMessage(message)
                        .setButtonsJson(buttonsJson)
                        .build()
        ).getMessageId();
    }

    public String sendMessage(int userId, String message, String repliedMessageId) {
        return grpcClient.sendMessage(
                ChatServiceOuterClass.SendMessageRequest.newBuilder()
                        .setUserId(userId)
                        .setMessage(message)
                        .setRepliedMessageId(repliedMessageId)
                        .build()
        ).getMessageId();
    }

    public String editMessage(String messageId, String message) {
        return grpcClient.editMessage(
                ChatServiceOuterClass.EditMessageRequest.newBuilder()
                        .setMessageId(messageId)
                        .setMessage(message)
                        .build()
        ).getMessageId();
    }
}
