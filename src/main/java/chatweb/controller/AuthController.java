package chatweb.controller;

import chatweb.entity.Session;
import chatweb.repository.SessionRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class AuthController {
    private final SessionRepository sessionRepository;

    public String authorizeAndRedirect(HttpServletResponse response, int userId, String location) {
        Session session = new Session(UUID.randomUUID().toString(), userId);
        sessionRepository.save(session);
        response.addHeader("Set-Cookie", "sessionId=" + session.getId() + "; Path=/");
        return "redirect:" + location;
    }
}
