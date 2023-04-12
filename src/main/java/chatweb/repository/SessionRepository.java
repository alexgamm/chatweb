package chatweb.repository;

import chatweb.entity.Session;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SessionRepository {
    private final List<Session> sessions = new ArrayList<>();

    public void saveSession(Session session) {
        sessions.add(session);
    }

    public Session findSessionById(@Nullable String id) {
        if (id == null) {
            return null;
        }
        return sessions.stream()
                .filter(session -> session.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
