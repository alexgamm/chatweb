package chatweb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reactions")
@Getter
public class Reaction {
    @Id
    @Column(name = "id")
    private String id;
    @Setter
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
    @Setter
    @Column(name = "user_id")
    private int userId;
    @Setter
    @Column(name = "reaction")
    private String reaction;
}
