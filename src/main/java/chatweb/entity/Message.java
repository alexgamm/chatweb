package chatweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
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

    @Setter
    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Setter
    @OneToMany(mappedBy = "message")
    private Set<Reaction> reactions;

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "replied_message_id", referencedColumnName = "id")
    private Message repliedMessage;

    @Column(name = "send_date")
    private Date sendDate;

}