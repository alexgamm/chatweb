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

    public void addPlayer(Team team, User user, boolean leader) {
        teamRepository.removeLeaderFromAllTeams(team.getId(), user.getId());
        teamRepository.removePlayerFromAllTeams(team.getId(), user.getId());
        if (!team.getPlayers().contains(user)) {
            team.getPlayers().add(user);
        }
        if (team.isLeader(user) && !leader) {
            team.setLeader(null);
        } else if (!team.isLeader(user) && leader) {
            team.setLeader(user);
        }
        teamRepository.save(team);
    }

    public void removePlayer(Team team, User user) {
        team.getPlayers().remove(user);
        if (team.isLeader(user)) {
            team.setLeader(null);
        }
        teamRepository.save(team);
    }
}
