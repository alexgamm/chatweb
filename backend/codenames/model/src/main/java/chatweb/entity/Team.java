package chatweb.entity;

import chatweb.model.Color;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    @JsonIgnore
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

    public boolean isLeader(Integer userId) {
        if (leader == null) return false;
        return leader.getId().equals(userId);
    }

}
