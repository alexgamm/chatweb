package chatweb.repository;

import chatweb.entity.Game;
import chatweb.model.game.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Game g SET g.settings = :settings WHERE g.id = :gameId")
    void updateSettings(String gameId, Settings settings);
}