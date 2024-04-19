package chatweb.entity;

import chatweb.model.message.Button;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
@Getter
public class Message {
    @Id
    @Column(name = "id")
    private String id;

    @Setter
    @Column(name = "message")
    private String message;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @Setter
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Setter
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reaction> reactions;

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "replied_message_id", referencedColumnName = "id")
    private Message repliedMessage;

    @Column(name = "send_date")
    private Date sendDate;

    @Column(name = "buttons")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<Button> buttons;

    public Integer getRoomId() {
        return room == null ? null : room.getId();
    }

    public String getRoomKey() {
        return room == null ? null : room.getKey();
    }

}
