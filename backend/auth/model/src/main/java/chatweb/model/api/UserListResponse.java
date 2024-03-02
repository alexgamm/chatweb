package chatweb.model.api;

import chatweb.model.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class UserListResponse {

    private final List<User> users;

    @RequiredArgsConstructor
    @Getter
    public static class User {
        private final int id;
        private final String username;
        private final boolean online;
        private final Color color;
    }
}
