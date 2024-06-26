package chatweb.repository;

import chatweb.entity.Team;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeamRepository extends JpaRepository<Team, Integer> {

    @Modifying
    @Transactional
    @Query(
            value = "DELETE FROM codenames.team_players WHERE user_id = :userId AND team_id != :exceptTeamId",
            nativeQuery = true
    )
    void removePlayerFromAllTeams(Integer exceptTeamId, Integer userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Team t SET t.leader = null WHERE t.leader.id = :userId AND t.id <> :exceptTeamId")
    void removeLeaderFromAllTeams(Integer exceptTeamId, Integer userId);
}
