package chatweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reactions")
@Getter
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
