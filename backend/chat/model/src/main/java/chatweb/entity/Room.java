package chatweb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rooms")
@Getter
public class Room {

    @Id
    @SequenceGenerator(name = "rooms_id_seq", sequenceName = "rooms_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "rooms_id_seq", strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @Setter
    @Column(name = "key")
    private String key;

    @Column(name = "password")
    private String password;

    @OneToOne()
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "user_rooms",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users = new HashSet<>();

    public void addUser(User user) {
        users.add(user);
    }

}
