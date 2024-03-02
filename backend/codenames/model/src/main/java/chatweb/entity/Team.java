package chatweb.entity;

import chatweb.model.Color;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teams", schema = "codenames")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Team {
    @Id
    @SequenceGenerator(name = "teams_id_seq", schema = "codenames", sequenceName = "teams_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "teams_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Game game;

    @Setter
    @OneToMany
    @JoinTable(
            name = "team_players",
            schema = "codenames",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> players;

    private Color color;

    @OneToOne
    @JoinColumn(name = "leader_id", referencedColumnName = "id")
    @Nullable
    @Setter
    private User leader;

    public boolean isPlayer(User user) {
        return getPlayers().contains(user);
    }

    public boolean isLeader(User user) {
        return user.equals(getLeader());
    }

}
