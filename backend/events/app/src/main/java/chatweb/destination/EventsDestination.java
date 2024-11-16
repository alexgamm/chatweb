package chatweb.destination;

import chatweb.utils.RoomUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EventsDestination {
    @Nullable
    private final Integer roomId;

    public static EventsDestination fromString(@Nullable String destination) {
        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null");
        }
        String[] parts = destination.split("/");
        if (parts.length != 2 && parts.length != 3 && !parts[1].equals("events")) {
            throw new IllegalArgumentException("Invalid destination: " + destination);
        }
        if (parts.length == 2) {
            return new EventsDestination(null);
        }
        int roomId = RoomUtils.idFromKey(parts[2]);
        return new EventsDestination(roomId);
    }

    public static String getDestination(@Nullable String roomKey) {
        String destination = "/events";
        if (roomKey != null) {
            destination = String.format("%s/%s", destination, roomKey);
        }
        return destination;
    }
}
