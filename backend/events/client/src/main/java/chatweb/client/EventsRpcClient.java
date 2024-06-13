package chatweb.client;

import chatweb.events.EventsServiceGrpc;
import chatweb.events.EventsServiceOuterClass;
import chatweb.model.event.IEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class EventsRpcClient {

    private final ObjectMapper objectMapper;
    private final EventsServiceGrpc.EventsServiceBlockingStub grpcClient;

    public EventsRpcClient(
            ObjectMapper objectMapper,
            @GrpcClient("events") EventsServiceGrpc.EventsServiceBlockingStub grpcClient
    ) {
        this.objectMapper = objectMapper;
        this.grpcClient = grpcClient;
    }

    public Set<Integer> getOnlineUserIds() {
        return new HashSet<>(
                grpcClient
                        .getOnlineUserIds(Empty.getDefaultInstance())
                        .getOnlineUserIdsList()
        );
    }

    public void addEvent(IEvent event) {
        try {
            grpcClient.addEvent(
                    EventsServiceOuterClass.AddEventRequest.newBuilder()
                            .setEventJson(objectMapper.writeValueAsString(event))
                            .build()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
