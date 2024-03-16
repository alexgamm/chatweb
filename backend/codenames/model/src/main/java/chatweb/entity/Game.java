package chatweb.entity;

import chatweb.model.game.GameState;
import chatweb.model.game.Settings;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "games", schema = "codenames")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Game {
    @Id
    @Column(name = "id")
    private String id;

    @OneToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "host_id", referencedColumnName = "id")
    private User host;

    @Setter
    @ManyToMany
    @JoinTable(
            name = "game_viewers",
            schema = "codenames",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> viewers;

    @Setter
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams;

    @Type(JsonType.class)
    private Settings settings;

    @Setter
    @Type(JsonType.class)
    private GameState state;

    public boolean isViewer(User user) {
        return getViewers().contains(user);
    }

    public boolean isPlayer(User user) {
        return getTeams().stream().anyMatch(team -> team.isPlayer(user));
    }

    public List<Team> getShuffledTeams() {
        List<Team> shuffledTeams = new ArrayList<>(getTeams());
        Collections.shuffle(shuffledTeams);
        return shuffledTeams;
    }

}
