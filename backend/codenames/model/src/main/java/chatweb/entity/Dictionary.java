package chatweb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "dictionaries", schema = "codenames")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Dictionary {
    @Id
    @Column(name = "id")
    private String id;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> words;

    @Column(name = "name")
    private String name;

    public LinkedList<String> getRandomWords(int amount) {
        List<String> words = new ArrayList<>(getWords());
        Collections.shuffle(words);
        return new LinkedList<>(words.subList(0, amount));
    }
}
