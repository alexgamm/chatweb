package chatweb.entity;

import java.util.Date;

public class User {

    private final int id;
    private final String username;
    private final String email;
    private final String password;
    private final Date lastActivityAt;

    public User(int id, String username, String email, String password, Date lastActivityAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.lastActivityAt = lastActivityAt;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Date getLastActivityAt() {
        return lastActivityAt;
    }
}

