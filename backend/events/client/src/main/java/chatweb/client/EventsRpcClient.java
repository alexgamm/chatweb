package chatweb.client;

import chatweb.events.EventsServiceGrpc;
import com.google.protobuf.Empty;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class EventsRpcClient {

    private final EventsServiceGrpc.EventsServiceBlockingStub grpcClient;

    public EventsRpcClient(
            @GrpcClient("events") EventsServiceGrpc.EventsServiceBlockingStub grpcClient
    ) {
        this.grpcClient = grpcClient;
    }

    public Set<Integer> getOnlineUserIds() {
        return new HashSet<>(
                grpcClient
                        .getOnlineUserIds(Empty.getDefaultInstance())
                        .getOnlineUserIdsList()
        );
    }
}
