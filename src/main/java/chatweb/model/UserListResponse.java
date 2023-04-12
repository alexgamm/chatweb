package chatweb.model;

import java.util.Date;
import java.util.List;

public class UserListResponse {

    public static class User {
        private final String username;
        private final Date lastActivityAt;

        public User(String username, Date lastActivityAt) {
            this.username = username;
            this.lastActivityAt = lastActivityAt;
        }

        public String getUsername() {
            return username;
        }

        public Date getLastActivityAt() {
            return lastActivityAt;
        }
    }
    private final List<User> users;

    public UserListResponse(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }
}
