package chatweb.service;

import chatweb.entity.Team;
import chatweb.entity.User;
import chatweb.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    @Nullable
    public Team findTeam(Integer id) {
        return teamRepository.findById(id).orElse(null);
    }

    public Team addPlayer(Team team, User user, boolean leader) {
        teamRepository.removeLeaderFromAllTeams(user.getId());
        teamRepository.removePlayerFromAllTeams(user.getId());
        team.getPlayers().add(user);
        if (leader) {
            team.setLeader(user);
        }
        return teamRepository.save(team);
    }
}
