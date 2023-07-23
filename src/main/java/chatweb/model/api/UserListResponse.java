package chatweb.model.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;
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
        private final Date lastActivityAt;
        private final boolean online;
    }
}
