package chatweb.model.event;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserOnlineEvent extends UserEvent {
    private boolean online;

    public UserOnlineEvent(int userId, boolean online) {
        super(userId);
        this.online = online;
    }
}
